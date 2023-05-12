# assumes that you've created redis configurations on the same directory
# if not,
#   please try `brew services install redis`,
#   and then copy the configuration to the directory where you'll start this script.
redis-server 6379.conf &
redis-server 6380.conf &
redis-server 6381.conf &
redis-cli --cluster create 127.0.0.1:6379 127.0.0.1:6380 127.0.0.1:6381
