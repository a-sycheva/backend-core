@import ru.mentee.power.crm.model.Lead
@import ru.mentee.power.crm.model.LeadStatus
@param Lead lead

<!DOCTYPE html>
<html>
<head>
    <title>Редактирование лида</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100">
<div class="container mx-auto p-6">
    <h1 class="text-2xl font-bold mb-4">Редактирование лида</h1>
    <form method="post" action="/leads/${String.valueOf(lead.id())}" class="bg-white p-6 rounded shadow">
        <!-- Hidden field для ID -->
        <div>
            <input type="hidden" name="id" value="${String.valueOf(lead.id())}">
        </div>
        <div>
            <label for="name" class="block text-sm font-medium text-gray-700 mb-1">
                Company
            </label>
            <input
                    type="text"
                    id="name"
                    value="${lead.name()}"
                    name="name"
                    required
                    class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                    placeholder="Ivan"
            />
        </div>
        <div>
            <label for="email" class="block text-sm font-medium text-gray-700 mb-1">
                Email
            </label>
            <input
                    type="email"
                    id="email"
                    value="${lead.email()}"
                    name="email"
                    required
                    class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                    placeholder="example@company.com"
            />
        </div>
        <div>
            <label for="company" class="block text-sm font-medium text-gray-700 mb-1">
                Company
            </label>
            <input
                    type="text"
                    id="company"
                    value="${lead.company()}"
                    name="company"
                    required
                    class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                    placeholder="TestCorp"
            />
        </div>
        <div>
            <label for="status" class="block text-sm font-medium text-gray-700 mb-1">
                Status
                <select name = "status">
                    @if(lead.status() == LeadStatus.NEW)
                        <option value="NEW" selected>Новый</option>
                    @else
                        <option value="NEW">Новый</option>
                    @endif

                    @if(lead.status() == LeadStatus.CONTACTED)
                        <option value="CONTACTED" selected>Связались</option>
                    @else
                        <option value="CONTACTED">Связались</option>
                    @endif

                    @if(lead.status() == LeadStatus.QUALIFIED)
                        <option value="QUALIFIED" selected>Квалифицированный</option>
                    @else
                        <option value="QUALIFIED">Квалифицированный</option>
                    @endif
                </select>
            </label>
        </div>
        <button
                type="submit"
                class="w-full bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600 focus:ring-2 focus:ring-blue-500"
        >
            Редактировать лида
        </button>
        <a
                href="/leads"
                class="block text-center text-sm text-gray-600 hover:text-gray-900"
        >
            Отмена
        </a>
    </form>
</div>
</body>
</html>