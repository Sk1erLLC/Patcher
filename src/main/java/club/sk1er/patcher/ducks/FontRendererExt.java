package club.sk1er.patcher.ducks;

import club.sk1er.patcher.hooks.FontRendererHook;

public interface FontRendererExt {
    FontRendererHook patcher$getFontRendererHook();
}
