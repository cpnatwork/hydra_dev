@echo off
rem SetLocal EnableDelayedExpansion

rem ***************************************************************************
rem * author: Christoph Peter Neumann
rem ***************************************************************************

echo.
echo INFO: executing %~n0

set PATH=%PROGRAMFILES%\7-Zip;%PATH%

rem Linux:
rem zip -vur scal_0.2.zip * -x@$0.exclude

rem * The 7-zip exclude switch -x (-xr recurse directory) with ! excludes 
rem * filenames based on a wildcard search. Without the backslash it is 
rem * trying to exclude filenames that match ?svn* (e.g. asvn.log);
rem * with the \* at the end mean don't include anything underneath 
rem * a directory matching ?svn

for /D %%D in (sys-???) do (
	cmd /D /C 7z u -r -tzip -y -x@%~n0.excludefiles -xr@%~n0.excludedirs "%%~D.zip" "%%~D"
)

pause