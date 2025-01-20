# This script should only be used to configure the dev and test environments.
# In production, secure secrets should be used.

export CSSM_RDBMS_PROTOCOL="postgresql"

export CSSM_RDBMS_PORT="5432"
export CSSM_RDBMS_USER="cssm-dev"
export CSSM_RDBMS_PWD="dev"

export CSSM_INT_TEST_RDBMS_USER="cssm-int-tester"
export CSSM_INT_TEST_RDBMS_PWD="int-test"

# This env var should NOT be set in production
export ALLOW_DROP_ALL="true"
