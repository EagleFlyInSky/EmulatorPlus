package com.eagle.emulator.plus.core;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eagle.emulator.HookParams;
import com.eagle.emulator.R;
import com.eagle.emulator.plus.overlay.OverlayConfigHook;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.hutool.core.io.FileUtil;
import de.robv.android.xposed.XposedBridge;

public class FontListAdapter extends RecyclerView.Adapter<FontListAdapter.FontViewHolder> {

    private final List<FontInfo> fontInfos = new ArrayList<>();

    private final LayoutInflater layoutInflater;

    private final OverlayConfigHook hook;

    private final FontData fontData;

    public void saveConfig() {
        fontInfos.stream().filter(FontInfo::isDefaultFont).findFirst().ifPresent(fontInfo -> {
            this.fontData.setDefaultFont(fontInfo.getTitle());
        });
        fontInfos.stream().filter(FontInfo::isCurrentFont).findFirst().ifPresent(fontInfo -> {
            this.fontData.getGameFonts().put(hook.getGameName(), fontInfo.getTitle());
        });
        TomlWriter writer = new TomlWriter();
    }


    public FontListAdapter(LayoutInflater layoutInflater, OverlayConfigHook overlayConfigHook) {
        this.layoutInflater = layoutInflater;
        this.hook = overlayConfigHook;

        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File fontDir = Paths.get(absolutePath, "Android", "data", HookParams.AZAHAR, "files", "fonts").toFile();

        if (!FileUtil.exist(fontDir)) {
            FileUtil.mkdir(fontDir);
        }

        Toml toml = new Toml();
        File configFile = Paths.get(fontDir.getAbsolutePath(), "fonts.toml").toFile();
        if (FileUtil.exist(configFile)) {
            toml.read(configFile);
            this.fontData = toml.to(FontData.class);
        } else {
            this.fontData = new FontData();
            TomlWriter writer = new TomlWriter();
            try {
                writer.write(fontData, configFile);
            } catch (IOException e) {
                XposedBridge.log(e);
            }
        }

        // 扫描文件夹路径下的文件夹
        String[] files = fontDir.list();
        if (files != null) {
            for (String filePath : files) {
                File file = Paths.get(fontDir.getAbsolutePath(), filePath).toFile();
                if (file.isDirectory()) {
                    String defaultFont = this.fontData.getDefaultFont();
                    boolean isDefaultFont = defaultFont != null && defaultFont.equals(file.getName());

                    boolean isCurrentFont = false;
                    Map<String, String> gameFonts = this.fontData.getGameFonts();
                    if (gameFonts != null) {
                        String value = gameFonts.get(file.getName());
                        if (value != null && value.equals(file.getName())) {
                            isCurrentFont = true;
                        }
                    }

                    fontInfos.add(new FontInfo(file.getName(), file.getAbsolutePath(), isDefaultFont, isCurrentFont));
                }
            }
        }


    }


    @NonNull
    @Override
    public FontListAdapter.FontViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.font_item, parent, false);
        return new FontViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FontListAdapter.FontViewHolder holder, int position) {
        FontInfo fontInfo = fontInfos.get(position);
        holder.titleTextView.setText(fontInfo.getTitle());

        View itemView = holder.itemView;

        View btnDelete = itemView.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> deleteItem(position));

        View btnEdit = itemView.findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(v -> editItem(position));
    }

    private void deleteItem(int position) {
        this.fontInfos.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, fontInfos.size());
    }

    private void editItem(int position) {
        XposedBridge.log("编辑项目：" + position);
    }

    @Override
    public int getItemCount() {
        return fontInfos.size();
    }

    public static class FontViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;

        public FontViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.font_title);
        }
    }

}


