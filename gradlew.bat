@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem
@rem SPDX-License-Identifier: Apache-2.0
@rem

@if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
set APP_HOME=%DIRNAME%
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi
set WRAPPER_PROPERTIES=%APP_HOME%\gradle\wrapper\gradle-wrapper.properties
if not exist "%WRAPPER_PROPERTIES%" (
    echo ERROR: Cannot read %WRAPPER_PROPERTIES% 1>&2
    goto fail
)

set PS_COMMAND=^
$ErrorActionPreference = 'Stop';^
$props = Get-Content -Raw '%WRAPPER_PROPERTIES%';^
$map = @{};^
foreach ($line in $props -split "`n") {^
    if ($line -match '^[^#]+?=') {^
        $parts = $line.Split('=', 2);^
        $map[$parts[0]] = $parts[1].Replace('\', '');^
    }^
}^
function Get-BaseDir($name) {^
    param([string]$name)^
    $appHome = '%APP_HOME%'^
    switch ($name) {^
        'GRADLE_USER_HOME' { if ($env:GRADLE_USER_HOME) { return $env:GRADLE_USER_HOME } else { return Join-Path $env:USERPROFILE '.gradle' } }^
        'PROJECT' { return $appHome }^
        default { return $appHome }^
    }^
}^
$distributionUrl = $map['distributionUrl'];^
if (-not $distributionUrl) { throw 'distributionUrl is not set' }^
$distributionBase = if ($map.ContainsKey('distributionBase')) { $map['distributionBase'] } else { 'PROJECT' };^
$distributionPath = if ($map.ContainsKey('distributionPath')) { $map['distributionPath'] } else { 'wrapper/dists' };^
$zipStoreBase = if ($map.ContainsKey('zipStoreBase')) { $map['zipStoreBase'] } else { 'PROJECT' };^
$zipStorePath = if ($map.ContainsKey('zipStorePath')) { $map['zipStorePath'] } else { 'wrapper/dists' };^
$distributionName = [System.IO.Path]::GetFileNameWithoutExtension($distributionUrl);^
$hashProvider = [System.Security.Cryptography.SHA256]::Create();^
$hashBytes = $hashProvider.ComputeHash([System.Text.Encoding]::UTF8.GetBytes($distributionUrl));^
$distributionHash = ([System.BitConverter]::ToString($hashBytes)).Replace('-', '').ToLower();^
$distDir = Join-Path (Join-Path (Get-BaseDir $distributionBase) $distributionPath) (Join-Path $distributionName $distributionHash);^
$zipDir = Join-Path (Join-Path (Get-BaseDir $zipStoreBase) $zipStorePath) $distributionName;^
$zipFile = Join-Path $zipDir ($distributionHash + '.zip');^
if (-not (Test-Path $zipDir)) { New-Item -ItemType Directory -Path $zipDir | Out-Null };^
if (-not (Test-Path $zipFile)) {^
    if (-not (Test-Path $zipDir)) { New-Item -ItemType Directory -Path $zipDir | Out-Null }^
    Invoke-WebRequest -UseBasicParsing -Uri $distributionUrl -OutFile $zipFile^
}^
if (-not (Test-Path $distDir)) {^
    if (Test-Path $distDir) { Remove-Item -Recurse -Force $distDir }^
    New-Item -ItemType Directory -Path $distDir | Out-Null^
    Expand-Archive -Path $zipFile -DestinationPath $distDir -Force^
}^
$gradleHome = Get-ChildItem -Path $distDir -Directory -Filter 'gradle-*' | Select-Object -First 1;^
if (-not $gradleHome) { throw 'Unable to locate extracted Gradle distribution' }^
Write-Output (Join-Path $gradleHome.FullName 'bin\gradle.bat')

for /f "usebackq tokens=*" %%i in (`powershell -NoLogo -NoProfile -Command "%PS_COMMAND%"`) do set GRADLE_CMD=%%i
if not "%ERRORLEVEL%"=="0" goto fail
if not exist "%GRADLE_CMD%" (
    echo ERROR: Unable to locate Gradle executable 1>&2
    goto fail
)

"%GRADLE_CMD%" %*
set EXIT_CODE=%ERRORLEVEL%
if "%OS%"=="Windows_NT" endlocal
exit /b %EXIT_CODE%

:fail
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if "%OS%"=="Windows_NT" endlocal
exit /b %EXIT_CODE%
