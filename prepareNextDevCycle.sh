# Prepare for a new development stream, inidivdually for SWT

make_common_mak='bundles/org.eclipse.swt/Eclipse SWT/common/library/make_common.mak'
source "${make_common_mak}"
new_min_ver=$((min_ver + 1))
echo "New minor version of library: ${new_min_ver}"

sed -i "${make_common_mak}" \
	--expression "s|min_ver=${min_ver}|min_ver=${new_min_ver}|g" \
	--expression "s|rev=[0-9]\+|rev=0|g"

sed -i 'bundles/org.eclipse.swt/Eclipse SWT PI/common/org/eclipse/swt/internal/Library.java' \
	--expression "s|MINOR_VERSION = ${min_ver};|MINOR_VERSION = ${new_min_ver};|g" \
	--expression "s|REVISION = [0-9]\+;|REVISION = 0;|g"

echo "version ${maj_ver}.${new_min_ver}" > 'bundles/org.eclipse.swt/Eclipse SWT/common/version.txt'
	

git commit --all --message "Configure SWT build scripts for ${NEXT_RELEASE_VERSION}"
