@echo off
setlocal

set ENV_FILE=".env"

pushd %USERPROFILE%\IdeaProjects\FileRocket\ || (echo "Директория не найдена." & exit /b 1)

git fetch origin
call git checkout master || (echo "Ветка 'master' не найдена." & exit /b 1)

call git pull origin master || (echo "Ошибка при обновлении ветки 'master'." & exit /b 1)

if not exist "%CD%\%ENV_FILE%" (
    echo "Не найден файл окружения: %ENV_FILE%"
    exit /b 1
)

call docker compose -f docker-compose.yml --env-file %ENV_FILE% down --timeout=60 --remove-orphans || (echo "Ошибка при остановке контейнеров." & exit /b 1)
call docker compose -f docker-compose.yml --env-file %ENV_FILE% up --build --detach || (echo "Ошибка при запуске контейнеров." & exit /b 1)

popd || (echo "Не удалось вернуться в предыдущую директорию." & exit /b 1)

echo "Деплой завершён успешно."
endlocal
