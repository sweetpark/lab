package com.example.playground.wiezon.context.patcher;

import com.example.playground.wiezon.context.Profile;
import com.example.playground.wiezon.context.TemplateContext;

public interface Patcher {
    void patch(TemplateContext context, Profile profile);
}
