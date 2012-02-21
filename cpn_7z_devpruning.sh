#!/bin/zsh
for iter_dir in `find . -name '[^.]*' -type d -maxdepth 1 -mindepth 1`; do
	echo INFO: zipping $iter_dir
	7z u -r -tzip -y -x@"`basename $0`.excludefiles" -xr@"`basename $0`.excludefiles" "$iter_dir.zip" "$iter_dir"
done