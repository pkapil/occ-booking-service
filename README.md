# Optimistic Concurrency Control - Booking Service
1. On Bootup jobs are loaded in table
2. Several threads compete to finish the jobs.
3. Threads contend with each other and  only one thread will be able to acquire lock on job and finally gets chance to perform job.
4. Other threads competed will fail.

### Steps to Check:
1. Login to ```http://localhost:8080/h2-console``` using password ```password```.
2. Check Jobs from the console.
3. Start the jobs by ```http://localhost:8080/start```.
