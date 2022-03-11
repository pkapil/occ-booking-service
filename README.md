# Optimistic Concurrency Control - Booking Service

### 1. Problem: Single Huge table with records need to be read as batches from concurrent nodes
### 2. Problem: Concurrent nodes resticted to read chuncks 
### Steps to Check:
1. Login to ```http://localhost:8080/h2-console``` using password ```password```.
2. Check Jobs from the console.
3. Start the job consumption by ```http://localhost:8080/startp```.
3. Start the job contention by ```http://localhost:8080/startex```.