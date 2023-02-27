# Overview
Test assignment for student result compilation for MyWhoosh. This will cover below points.
1. Crud operation for student using rest endpoints.
2. Crud operation for result using rest endpoints.
3. Calculate position of student which will be depending on the number of marks. Student with highest
   marks will be on number one position.
4. The Passing marks percentage will be 50 and remarks will be passed/failed
   depending on the total marks and obtained marks field values.
5. Roll Number must be validated from students’ collection with active status and
   Grade.
6. Student records will be inserted with Active status.
7. Soft deleting the student that will update the status of the student to
   deleted status.
8. Only registered users can perform all above mentioned actions.
9. Rest endpoint for getting result of student. 

# Tech Stack:
- Spring Boot Reactive (Java)
- Spring Security (JWT strategy)
- MongoDB
- Java 17
- Gradle

# To run application locally
`./gradlew bootRun`

# To run all unit testcases
`./gradlew clean test`

# Curl request for generating token.
`curl --location --request POST 'http://localhost:8083/login' \
--header 'Content-Type: application/json' \
--data-raw '{
"username" : "admin",
"password" : "admin"
}'`

**Note** : There are two credentials exists in system which can generate token.
1. Username : **admin** and password : **admin**
2. Username : **user** and password : **user**

# Example curl request for creating student.
`curl --location --request POST 'localhost:8083/students' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlIjpbIlJPTEVfVVNFUiJdLCJzdWIiOiJ1c2VyIiwiaWF0IjoxNjc3NDc5Mjg0LCJleHAiOjE2Nzc1MDgwODR9.RVmzstxiEBCbKmrd1PLUktxEfns8jmwlVO7qmalosevYgsiONFh4LJ-nj8JCYKIMX2UopgQzGhTfw2Pqg-R8jw' \
--header 'Content-Type: application/json' \
--data-raw '{
"name": "James",
"roll_number": 13,
"father_name": "James Father",
"grade": 8
}'`

# Example curl request for creating result.
`curl --location --request POST 'localhost:8083/results' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlIjpbIlJPTEVfQURNSU4iXSwic3ViIjoiYWRtaW4iLCJpYXQiOjE2Nzc0ODc5MjcsImV4cCI6MTY3NzUxNjcyN30.43_sJS5Mudlrc6H6Jvng2l6mPYKDQ9CQABY_UlCwgWXCWE32fw4wI4Qjm-Hpb7tKsIzLRniBIq1VhrlohdTtbQ' \
--header 'Content-Type: application/json' \
--data-raw '{
"total_marks" : 100,
"obtained_marks" : 25,
"roll_number" : 11,
"grade" : 8
}'`

