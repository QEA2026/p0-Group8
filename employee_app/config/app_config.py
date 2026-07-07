import os
from pathlib import Path

from dotenv import load_dotenv

load_dotenv(Path(__file__).resolve().parents[1] / ".env")


class AppConfig:
    def __init__(self):
        self.jwt_secret = self._get_required("JWT_SECRET")
        self.jwt_expiration_hours = self._get_required_int("JWT_EXPIRATION_HOURS")

    def _get_required(self, key):
        value = os.getenv(key)

        if not value:
            raise ValueError(f"{key} environment variable is required.")

        return value

    def _get_required_int(self, key):
        value = self._get_required(key)

        try:
            return int(value)
        except ValueError:
            raise ValueError(f"{key} must be an integer.")