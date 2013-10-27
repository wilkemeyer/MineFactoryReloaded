git clone https://github.com/denoflionsx/MFR-Forestry.git
foo=$(head -1 MFR-Forestry_Revision.txt)
echo $foo
cd MFR-Forestry
git checkout $foo
git submodule init
git submodule update