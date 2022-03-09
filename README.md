# Optimistic Concurrency Control - Booking Service
1. On Bootup jobs are loaded in table
2. Several threads compete to finish the jobs, between contention only one job succeeds and able to finish rest of the jobs fail

### Steps to Check:
1. Login to ```http://localhost:8080/h2-console``` using password ```password```.
2. Check Jobs from the console.
3. Start the jobs by ```http://localhost:8080/start```.