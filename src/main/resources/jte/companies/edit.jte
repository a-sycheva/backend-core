@import java.util.List
@import ru.mentee.power.crm.model.Company


@param Company company

<!DOCTYPE html>
<html>
<head>
    <title>Редактирование компании</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100">
<div class="container mx-auto p-6">
    <h1 class="text-2xl font-bold mb-4">Редактирование компании</h1>
    <form method="post" action="/companies/${String.valueOf(company.getId())}" class="bg-white p-6 rounded shadow">
        <!-- Hidden field для ID -->
        <div>
            <input type="hidden" name="id" value="${String.valueOf(company.getId())}">
        </div>
        <div>
            <label for="name" class="block text-sm font-medium text-gray-700 mb-1">
                Name
            </label>
            <input
                    type="text"
                    id="name"
                    value="${company.getName()}"
                    name="name"
                    required
                    class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                    placeholder="Inc"
            />
        </div>
        <div>
            <label for="industry" class="block text-sm font-medium text-gray-700 mb-1">
                Industry
            </label>
            <input
                    type="text"
                    id="industry"
                    value="${company.getIndustry()}"
                    name="industry"
                    required
                    class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                    placeholder="IT"
            />
        </div>
        <button
                type="submit"
                class="w-full bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600 focus:ring-2 focus:ring-blue-500"
        >
            Редактировать компанию
        </button>
        <a
                href="/companies"
                class="block text-center text-sm text-gray-600 hover:text-gray-900"
        >
            Отмена
        </a>
    </form>
</div>
</body>
</html>