#!/usr/bin/env bash
# macOS launcher for the Slay The Dungeon art-tool
# Double-clickable .command file for Finder. Requires executable permission: chmod +x start.command

set -euo pipefail
cd "$(dirname "$0")"

# Use a project-local virtual environment to avoid system-managed pip issues
VENV_DIR=".venv"
if [ ! -d "$VENV_DIR" ]; then
  echo "Creating virtual environment in $VENV_DIR..."
  if command -v python3 >/dev/null 2>&1; then
    python3 -m venv "$VENV_DIR"
  elif command -v python >/dev/null 2>&1; then
    python -m venv "$VENV_DIR"
  else
    echo "Python not found. Please install Python 3.10+ from https://www.python.org/"
    exit 1
  fi
fi

echo "Activating virtual environment..."
source "$VENV_DIR/bin/activate"

echo "Installing dependencies into virtualenv..."
python -m pip install --upgrade pip -q
python -m pip install -r requirements.txt || {
  echo "Failed to install dependencies into virtualenv. Check output above for details."
  exit 1
}

echo "Starting Slay The Dungeon art-tool (FastAPI + Uvicorn)..."
# open the browser in background
open "http://127.0.0.1:8765" || true

# Run the server (blocks). Ctrl-C to stop.
exec python -m uvicorn server.main:app --host 127.0.0.1 --port 8765
