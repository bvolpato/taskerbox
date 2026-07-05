#!/bin/bash
#
# Copyright (C) 2015 Bruno Candido Volpato da Cunha (brunocvcunha@gmail.com)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

usage() {
  cat <<'EOF'
Start Taskerbox server.

Runs web/target/taskerbox.jar with Dropwizard config web/taskerbox.yml.

Usage:
  ./start.sh

Options:
  -h, --help      Show this help.

Available sessions:
  ./start.sh      Start Taskerbox Dropwizard server.
EOF
}

case "${1:-}" in
  -h|--help)
    usage
    exit 0
    ;;
  "")
    ;;
  *)
    echo "Unknown arg: $1" >&2
    usage >&2
    exit 1
    ;;
esac

(cd web; java -jar target/taskerbox.jar server taskerbox.yml)
