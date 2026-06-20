@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo 正在检查 Python 依赖...
python -m pip install -r requirements.txt -q
if errorlevel 1 (
    echo 请先安装 Python 3.10+ 并确保 python 在 PATH 中。
    pause
    exit /b 1
)

echo 启动卡牌美术工具...
start "" "http://127.0.0.1:8765"
python -m uvicorn server.main:app --host 127.0.0.1 --port 8765
