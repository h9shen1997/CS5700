import math
import time
from socket import *

socket_timeout = 1  # 1 second timeout
localhost = '127.0.0.1'
port_number = 12000
start_seq_number = 1
end_seq_number = 10
buffer_size = 1024
second_to_millisecond = 1000
total_run = 10

client_socket = socket(AF_INET, SOCK_DGRAM)
client_socket.settimeout(socket_timeout)  # set 1 second timeout

# optional exercise
max_rtt = -math.inf
min_rtt = math.inf
sum_rtt = 0
success_count = 0

for i in range(start_seq_number, end_seq_number + 1):
    try:
        message = 'Ping {sequence_number} {time}'
        start_time = time.time()
        client_socket.sendto(message.format(sequence_number=i, time=start_time).encode('utf_8'),
                             (localhost, port_number))
        response, address = client_socket.recvfrom(buffer_size)
        rtt = (time.time() - start_time) * second_to_millisecond  # convert to ms
        print(response.decode('utf_8'))
        print(address)
        print(f'The RTT is {rtt}ms')
        sum_rtt += rtt
        success_count += 1
        max_rtt = max(max_rtt, rtt)
        min_rtt = min(min_rtt, rtt)
    except TimeoutError:
        print('Request timed out')
        continue

avg_rtt = sum_rtt / success_count
loss_percent = (total_run - success_count) / total_run * 100

print(f'The maximum rtt is {max_rtt}ms')
print(f'The mininum rtt is {min_rtt}ms')
print(f'The average rtt is {avg_rtt}ms')
print(f'The packet loss percentage is {loss_percent}%')
