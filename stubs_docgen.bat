@echo off
set CUR_DIR=%CD%
set CUR_FILE_DIR=%~dp0
cd %CUR_FILE_DIR%
@echo on

rd /s /q stubs
mkdir stubs
wsimport wsdl/DocGenService.wsdl -s stubs  -B-XautoNameResolution -Xnocompile
wsimport wsdl/partner.wsdl -s stubs  -B-XautoNameResolution -Xnocompile
pause