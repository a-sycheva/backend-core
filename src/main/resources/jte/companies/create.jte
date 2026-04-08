@import ru.mentee.power.crm.model.Company

@param Company company

@template.layout.main(
content = @`
    <div class="max-w-md mx-auto mt-8">
        <h1 class="text-2xl font-bold mb-6">Добавить новую компанию</h1>

        <form action="/companies" method="post" class="space-y-4">
            <!-- Email -->
            <div>
                <div>
                    <label for="name" class="block text-sm font-medium text-gray-700 mb-1">
                        Name
                    </label>
                    <input
                            type="text"
                            id="name"
                            name="name"
                            required
                            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                            placeholder="Inc"
                    />
                </div>
                <label for="industry" class="block text-sm font-medium text-gray-700 mb-1">
                    Industry
                </label>
                <input
                        type="text"
                        id="industry"
                        name="industry"
                        required
                        class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                        placeholder="IT"
                />
            </div>
            <!-- Submit Button -->
            <button
                    type="submit"
                    class="w-full bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600 focus:ring-2 focus:ring-blue-500"
            >
                Создать компанию
            </button>
            <!-- Cancel Link -->
            <a
                    href="/companies"
                    class="block text-center text-sm text-gray-600 hover:text-gray-900"
            >
                Отмена
            </a>
        </form>
    </div>
`
)