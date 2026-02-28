

BASE_URL="http://localhost:8090/api"
TOKEN=""

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  School System API - Тестирование${NC}"
echo -e "${BLUE}========================================${NC}\n"

# 1. Регистрация
echo -e "${YELLOW}1. Регистрация нового пользователя...${NC}"
REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"login":"testuser","password":"password123"}')

echo -e "${GREEN}Ответ:${NC}"
echo "$REGISTER_RESPONSE" | jq '.' 2>/dev/null || echo "$REGISTER_RESPONSE"

# Извлечение токена
TOKEN=$(echo "$REGISTER_RESPONSE" | jq -r '.token' 2>/dev/null)
if [ -z "$TOKEN" ] || [ "$TOKEN" = "null" ]; then
  echo -e "${RED}Ошибка при регистрации!${NC}"
  exit 1
fi
echo -e "${GREEN}Токен получен: ${TOKEN:0:20}...${NC}\n"

# 2. Вход
echo -e "${YELLOW}2. Вход в систему...${NC}"
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","password":"123"}')

echo -e "${GREEN}Ответ:${NC}"
echo "$LOGIN_RESPONSE" | jq '.' 2>/dev/null || echo "$LOGIN_RESPONSE"

# Обновление токена
TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.token' 2>/dev/null)
echo -e "${GREEN}Токен обновлен: ${TOKEN:0:20}...${NC}\n"

# 3. Получить всех студентов
echo -e "${YELLOW}3. Получить всех студентов...${NC}"
STUDENTS_RESPONSE=$(curl -s -X GET "$BASE_URL/students" \
  -H "Authorization: Bearer $TOKEN")

echo -e "${GREEN}Ответ:${NC}"
echo "$STUDENTS_RESPONSE" | jq '.' 2>/dev/null || echo "$STUDENTS_RESPONSE"
echo ""

# 4. Создать нового студента
echo -e "${YELLOW}4. Создать нового студента...${NC}"
CREATE_STUDENT=$(curl -s -X POST "$BASE_URL/students" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 100,
    "name": "Тестовый студент",
    "grade": "12-А"
  }')

echo -e "${GREEN}Ответ:${NC}"
echo "$CREATE_STUDENT" | jq '.' 2>/dev/null || echo "$CREATE_STUDENT"
echo ""

# 5. Получить студента по ID
echo -e "${YELLOW}5. Получить студента по ID (1)...${NC}"
STUDENT_BY_ID=$(curl -s -X GET "$BASE_URL/students/1" \
  -H "Authorization: Bearer $TOKEN")

echo -e "${GREEN}Ответ:${NC}"
echo "$STUDENT_BY_ID" | jq '.' 2>/dev/null || echo "$STUDENT_BY_ID"
echo ""

# 6. Обновить студента
echo -e "${YELLOW}6. Обновить студента (ID 1)...${NC}"
UPDATE_STUDENT=$(curl -s -X PUT "$BASE_URL/students/1" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Мейремхан Асель (обновлено)",
    "grade": "10-Б"
  }')

echo -e "${GREEN}Ответ:${NC}"
echo "$UPDATE_STUDENT" | jq '.' 2>/dev/null || echo "$UPDATE_STUDENT"
echo ""

# 7. Поиск студентов
echo -e "${YELLOW}7. Поиск студентов по имени 'Асель'...${NC}"
SEARCH_STUDENTS=$(curl -s -X GET "$BASE_URL/students/search?name=Асель" \
  -H "Authorization: Bearer $TOKEN")

echo -e "${GREEN}Ответ:${NC}"
echo "$SEARCH_STUDENTS" | jq '.' 2>/dev/null || echo "$SEARCH_STUDENTS"
echo ""

# 8. Получить всех учителей
echo -e "${YELLOW}8. Получить всех учителей...${NC}"
TEACHERS_RESPONSE=$(curl -s -X GET "$BASE_URL/teachers" \
  -H "Authorization: Bearer $TOKEN")

echo -e "${GREEN}Ответ:${NC}"
echo "$TEACHERS_RESPONSE" | jq '.' 2>/dev/null || echo "$TEACHERS_RESPONSE"
echo ""

# 9. Создать нового учителя
echo -e "${YELLOW}9. Создать нового учителя...${NC}"
CREATE_TEACHER=$(curl -s -X POST "$BASE_URL/teachers" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 100,
    "name": "Тестовый учитель",
    "subject": "Информатика"
  }')

echo -e "${GREEN}Ответ:${NC}"
echo "$CREATE_TEACHER" | jq '.' 2>/dev/null || echo "$CREATE_TEACHER"
echo ""

# 10. Выход
echo -e "${YELLOW}10. Выход из системы...${NC}"
LOGOUT_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/logout" \
  -H "Authorization: Bearer $TOKEN")

echo -e "${GREEN}Ответ:${NC}"
echo "$LOGOUT_RESPONSE" | jq '.' 2>/dev/null || echo "$LOGOUT_RESPONSE"
echo ""

echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}✅ Тестирование завершено!${NC}"
echo -e "${BLUE}========================================${NC}"

