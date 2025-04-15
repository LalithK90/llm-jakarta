#!/bin/bash
# BookResource API Test Script
# This script contains curl commands to test all endpoints of the BookResource API

# Base URL - replace with your actual server address and port
BASE_URL="http://localhost:8080/api"

echo "=== Testing BookResource API ==="

# 1. Get all books
echo -e "\n--- GET ALL BOOKS ---"
curl -X GET "${BASE_URL}/books" \
  -H "Accept: application/json" \
  -v

# 2. Get a specific book by ISBN
echo -e "\n--- GET BOOK BY ISBN ---"
curl -X GET "${BASE_URL}/books/1234567890" \
  -H "Accept: application/json" \
  -v

# 3. Get all categories
echo -e "\n--- GET ALL CATEGORIES ---"
curl -X GET "${BASE_URL}/books/categories" \
  -H "Accept: application/json" \
  -v

# 4. Get books by category
echo -e "\n--- GET BOOKS BY CATEGORY ---"
curl -X GET "${BASE_URL}/books/category/fiction" \
  -H "Accept: application/json" \
  -v

# 5. Search for books
echo -e "\n--- SEARCH BOOKS ---"
curl -X GET "${BASE_URL}/books/search?query=java" \
  -H "Accept: application/json" \
  -v

# 6. Create a new book
echo -e "\n--- CREATE NEW BOOK ---"
curl -X POST "${BASE_URL}/books" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "isbn": "9781234567897",
    "title": "Jakarta EE Essentials",
    "author": "Java Developer",
    "description": "A comprehensive guide to Jakarta EE",
    "price": 29.99,
    "stockQuantity": 50,
    "category": "Programming",
    "imageUrl": "/images/jakarta-ee.png"
  }' \
  -v

# 7. Update an existing book
echo -e "\n--- UPDATE BOOK ---"
curl -X PUT "${BASE_URL}/books/9781234567897" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "isbn": "9781234567897",
    "title": "Jakarta EE Essentials - 2nd Edition",
    "author": "Java Developer",
    "description": "Updated guide to Jakarta EE",
    "price": 34.99,
    "stockQuantity": 75,
    "category