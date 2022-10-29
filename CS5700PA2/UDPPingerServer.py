import random
from socket import *

server_socket = socket(AF_INET, SOCK_DGRAM)
server_socket.bind(('', 12000))

while True:
    rand = random.randint(0, 10)
    message, address = server_socket.recvfrom(1024)
    message = message.upper()

    if rand < 4:
        continue
    server_socket.sendto(message, address)
