## 繁體中文

### 概覽

PixelmonSync 用於同步多個 Pixelmon 伺服器的玩家資料。它使用 MySQL 將玩家資料寫入資料庫，確保伺服器間資料的一致性。僅在玩家登入、登出或執行保存指令時進行資料操作。

### 相容性

目前僅支持 Pixelmon 1.16.5 9.1.11 和 Java 11+。

### 安裝方式

1. 將 `.jar` 檔案放入 `mods` 資料夾。
2. 啟動伺服器後關閉，這會在 `config` 資料夾中生成 `pixelmonsync-common.toml` 檔案。
3. 設定 `pixelmonsync-common.toml` 內的 MySQL 資訊。
4. 重新啟動伺服器。

享受跨 Pixelmon 伺服器的無縫資料同步！
