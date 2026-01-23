import subprocess, json, pymysql, glob, os
from datetime import datetime, timedelta

# =========================================================
# [Utils] JSON 파싱 및 헬퍼 함수
# =========================================================
def parse_json_from_stdout(stdout: str):
    for line in stdout.splitlines():
        line = line.strip()
        if line.startswith("{") and line.endswith("}"):
            return json.loads(line)
    raise ValueError(f"JSON not found in stdout: \n {stdout}")

def is_empty_value(v):
    if v is None:
        return True
    if isinstance(v, str) and v.strip() == "":
        return True
    return False


# =========================================================
# [KMS] 암호화 및 보안 모듈 호출 (Java Process)
# =========================================================
def kms_call_enc(column, value):
    proc = subprocess.run(
        [
            "java", "-jar", "../playground-kms/build/libs/playground-kms.jar", column, value
        ],
        capture_output=True,
        text=True
    )

    if proc.returncode != 0:
        raise RuntimeError(proc.stderr)

    result = parse_json_from_stdout(proc.stdout)

    if not result["success"]:
        raise RuntimeError(result.get("message"))

    return result["encrypt"], result["hash"]

def kms_call_pw(value):
    proc = subprocess.run(
        [
            "java","-jar","../playground-kms/build/libs/playground-kms.jar", "password", value, "-pw"
        ],
        capture_output=True,
        text=True
    )


    if proc.returncode != 0:
        raise RuntimeError(proc.stderr)

    result = parse_json_from_stdout(proc.stdout)

    if not result["success"]:
        raise RuntimeError(result.get("message"))

    return result["password"], result["otpPassword"]


# =========================================================
# [Data Builder] 데이터 생성 및 설정 로드
# =========================================================
def build_insert_data(row: dict):
    data = {}

    for col, spec in row.items():
        value = spec.get("value")
        crypto = spec.get("crypto")

        if value is None:
            continue

        if crypto == "ENC_HASH":
            enc, hsh = kms_call_enc(col, str(value))
            data[col] = value
            data[f"{col}_ENC"] = enc
            data[f"{col}_HASH"] = hsh

        elif crypto == "ENC":
            enc, _ = kms_call_enc(col, str(value))
            data[col] = value
            data[f"{col}_ENC"] = enc

        elif crypto == "HASH":
            _, hsh = kms_call_enc(col, str(value))
            data[col] = value
            data[f"{col}_HASH"]= hsh

        elif crypto == "PASSWORD":
            pw, _ = kms_call_pw(str(value))
            data[col] = pw

        elif crypto == "OTP":
            _, otpPw = kms_call_pw(str(value))
            data[col] = otpPw

        else:
            now = datetime.now()

            if value == "CUR_YYMMDD":
                data[col] = now.strftime("%Y%m%d")

            elif value == "CUR_YYMMDDHHIISS":
                data[col] = now.strftime("%Y%m%d%H%M%S")

            elif value == "YESTER_YYMMDD":
                data[col] =  (now - timedelta(days=1)).strftime("%Y%m%d")

            else:
                data[col] = value

    return data



def load_table_json(path):
    with open(path, "r", encoding="utf-8") as f:
        return json.load(f)


# =========================================================
# [DB Connection] 데이터베이스 연결
# =========================================================
def get_conn():
    return pymysql.connect(
        host="192.168.0.101",
        port=3306,
        user="",
        password="",
        database="PayQ",
        charset="utf8mb4",
        autocommit=False
    )


# =========================================================
# [SQL Execution] INSERT 실행 로직
# =========================================================
def insert(conn, table, data: dict):
    data = {
        k: v
        for k, v in data.items()
        if not is_empty_value(v)
    }

    if not data:
        raise ValueError("INSERT data is empty")

    columns = ", ".join(data.keys())
    placeholders = ", ".join(["%s"] * len(data))
    values = list(data.values())

    sql = f"""
        INSERT INTO {table}
        ({columns})
        VALUES ({placeholders})
    """

    with conn.cursor() as cur:
        cur.execute(sql, values)

# =========================================================
# [Main] 실행 진입점
# =========================================================
if __name__ == "__main__": 

    base_dir = os.path.dirname(os.path.abspath(__file__))
    target_dir = os.path.join(base_dir, "data")
    target_pattern = os.path.join(target_dir, "*.json")
    
    json_files = glob.glob(target_pattern)
    
    print(f"--- [Init] Searching for JSON files in: {target_dir} ---")
    
    if not json_files:
        print(f"No JSON files found in {target_dir}")
    else:
        print(f"Found {len(json_files)} JSON files.")
        
        try:
            conn = get_conn()
            print("DB Connected.")
            try:
                for target_file in json_files:
                    file_name = os.path.basename(target_file)
                    print(f"\n--- [Start] Processing {file_name} ---")
                    
                    try:
                        # 1. 설정 파일 로드
                        table_spec = load_table_json(target_file)
                        
                        # 2. 테이블명 추출
                        table_name = table_spec.get("table")
                        if not table_name:
                            print(f"Skipping {file_name}: 'table' key is missing")
                            continue
                            
                        print(f"Target Table: {table_name}")

                        # 3. 데이터 빌드
                        for row in table_spec.get("rows"):
                            insert_data = build_insert_data(row)
                            print(f"Generated Data: {insert_data}")
                            insert(conn, table_name, insert_data)
                            print(f">>> [{file_name}] Insert & Commit Successful!")
                        conn.commit()
                    except Exception as e:
                        raise(f">>> [{file_name}] Error: {e}")

            except Exception as main_e:
                conn.rollback()
                print(f"--- [Fatal Error] {main_e} ---")
        finally:
            if conn:
                conn.close()
                print("\nDB Connection Closed.")