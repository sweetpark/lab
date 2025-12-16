package com.example.playground.fileIO.src.service;

import com.example.playground.fileIO.src.config.StorageProperties;
import com.example.playground.fileIO.src.entity.repository.StoreRepository;
import com.example.playground.fileIO.src.exception.StorageException;
import com.example.playground.fileIO.src.exception.StorageFileNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import com.example.playground.fileIO.src.entity.FileStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@Service
public class StoreServiceImpl implements StorageService{

    private final Path rootLocation;
    private final StoreRepository storeRepository;

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    public StoreServiceImpl(StorageProperties properties, StoreRepository storeRepository){
        this.rootLocation = Paths.get(properties.getLocation());
        this.storeRepository =storeRepository;
    }

    @Override
    public void init() {
        try{
            Files.createDirectories(rootLocation);
        }catch(FileAlreadyExistsException e){
            log.info("already exist dir");
        }
        catch(IOException e){
            throw new StorageException("Could not init", e);
        }
    }

    @Override
    public Resource loadResource(Long id) {
        try{
            FileStore fileStore = storeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("no content"));

            Path file = rootLocation.resolve(fileStore.getStoreFileName());
            return new UrlResource(file.toUri());
        }catch(MalformedURLException e){
            throw new StorageFileNotFoundException("could not read file : " + id, e);
        }
    }


    @Override
    public void save(MultipartFile file) {
        if(file.isEmpty()){
            throw new StorageException("failed to store empty file");
        }

        String ext = null;
        try{
            ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));
        }catch(NullPointerException e){
            throw new StorageException("File no exits", e);
        }


        FileStore store = FileStore.builder()
                .storeFileName(UUID.randomUUID().toString() + "." + ext)
                .uploadFileName(file.getOriginalFilename())
                .build();

        storeRepository.save(store);

        Path destinationFile = rootLocation.resolve(store.getStoreFileName());

        try(InputStream inputStream = file.getInputStream()){
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }catch(IOException e){
            throw new StorageException("Failed to store file", e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try{
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }catch (IOException e){
            throw new StorageException("fail to read store file", e);
        }
    }
}
