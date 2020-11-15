mkdir update
$update = Get-ChildItem Audiras*.zip
Expand-Archive $update.FullName update

rm runtime -Recurse
mv .\update\Audiras\runtime\ .\

$langfiles =Get-ChildItem .\update\Audiras\data\lang_*.txt
foreach ($file in $langfiles) { mv $file.FullName .\data\ -Force}

$licensefiles = Get-ChildItem .\update\Audiras\*.txt
foreach ($file in $licensefiles) { mv $file.FullName .\ -Force}

mv .\update\Audiras\README.md .\README.md -Force

rm update -Recurse
rm $update