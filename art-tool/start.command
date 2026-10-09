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

PORT=8765
PORT_PIDS="$(lsof -nP -tiTCP:"$PORT" -sTCP:LISTEN 2>/dev/null || true)"
if [ -n "$PORT_PIDS" ]; then
  echo "Port $PORT is occupied; stopping existing listener(s): $PORT_PIDS"
  for pid in $PORT_PIDS; do
    kill "$pid" 2>/dev/null || true
  done

  for attempt in {1..10}; do
    PORT_PIDS="$(lsof -nP -tiTCP:"$PORT" -sTCP:LISTEN 2>/dev/null || true)"
    [ -z "$PORT_PIDS" ] && break
    sleep 1
  done

  if [ -n "$PORT_PIDS" ]; then
    echo "Port $PORT is still occupied by process(es): $PORT_PIDS"
    exit 1
  fi
fi

echo "Starting Slay The Dungeon art-tool (FastAPI + Uvicorn)..."
# Start the browser after the server has bound the port, avoiding stale pages.
(until curl -fsS "http://127.0.0.1:$PORT/api/health" >/dev/null; do sleep 1; done; open "http://127.0.0.1:$PORT") &

# Run the server (blocks). Ctrl-C to stop.
exec python -m uvicorn server.main:app --host 127.0.0.1 --port "$PORT"
