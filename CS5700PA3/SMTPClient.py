import base64
import os
import ssl
from socket import *

import certifi

msg = "\r\n I love computer networks!"
end_msg = "\r\n.\r\n"
smtp_port = 587
buffer_size = 1024

# Choose a mail server and call it mail_server
# Choose outlook here because google no longer support unsecured access using email address and password
mail_server = "smtp.office365.com"  # this requires SSL authentication

# Create a socket called client_socket and establish a TCP connection with the mail_server
client_socket = socket(AF_INET, SOCK_STREAM)
client_socket.connect((mail_server, smtp_port))

# Print server response
print(client_socket.recv(buffer_size).decode())

# Send HELO command and print server response
client_socket.send('HELO Alice\r\n'.encode())
print(client_socket.recv(buffer_size).decode())

# Start TLS connection and print server response
client_socket.send('STARTTLS\r\n'.encode())
print(client_socket.recv(buffer_size).decode())

# Create an SSL context with local user certificate and wrap the client_socket with the SSL wrapper
ssl_context = ssl.SSLContext(ssl.PROTOCOL_TLS_CLIENT)
certificate_location = certifi.where()
ssl_context.load_verify_locations(certificate_location)
client_socket = ssl_context.wrap_socket(client_socket, server_hostname=mail_server)

# Resend HELO command. Reference: https://stackoverflow.com/questions/35188897/how-to-connect-to-office365-smtp-with-starttls-i-keep-getting-send-hello-first
client_socket.send('HELO Alice\r\n'.encode())
print(client_socket.recv(buffer_size).decode())

# Authenticate using user email and password
client_socket.send('AUTH LOGIN\r\n'.encode())
print(client_socket.recv(buffer_size).decode())
client_socket.send((base64.b64encode('shen.haot@outlook.com'.encode())) + '\r\n'.encode())
print(client_socket.recv(buffer_size).decode())
# The password is saved as an environment variable locally, so running this on your laptop will definitely fail
client_socket.send((base64.b64encode(os.getenv('OUTLOOK_PASSWORD').encode())) + '\r\n'.encode())
print(client_socket.recv(buffer_size).decode())

# Send MAIL FROM command and print server response.
client_socket.send('MAIL FROM: <shen.haot@outlook.com>\r\n'.encode())
print(client_socket.recv(buffer_size).decode())

# Send RCPT TO command and print server response.
client_socket.send('RCPT TO: <shen.haot@outlook.com>\r\n'.encode())
print(client_socket.recv(buffer_size).decode())

# Send DATA command and print server response.
client_socket.send('DATA\r\n'.encode())
print(client_socket.recv(buffer_size).decode())

# Send message data.
client_socket.send('Subject: CS5700 PA3 Test Email\r\n'.encode())
client_socket.send('From: shen.haot@outlook.com\r\n'.encode())
client_socket.send('To: shen.haot@outlook.com\r\n'.encode())
client_socket.send(msg.encode())
# Message ends with a single period.
client_socket.send(end_msg.encode())

# Print server response
print(client_socket.recv(buffer_size).decode())

# Send QUIT command and get server response.
client_socket.send('QUIT\r\n'.encode())
print(client_socket.recv(buffer_size).decode())

# Close the secured socket
client_socket.close()
