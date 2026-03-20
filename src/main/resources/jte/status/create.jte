@import ru.mentee.power.crm.model.Lead

@param Lead lead

@template.layout.main(
content = @`
    <div class="max-w-md mx-auto mt-8">
        <h1 class="text-2xl font-bold mb-6">Добавить нового статуса</h1>

        <form action="/statuses" method="post" class="space-y-4">
            <!-- Email -->
            <div>
                <label for="email" class="block text-sm font-medium text-gray-700 mb-1">
                    Status
                </label>
                <input
                        type="text"
                        id="status"
                        name="status"
                        required
                        class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                />
            </div>

            <!-- Submit Button -->
            <button
                    type="submit"
                    class="w-full bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600 focus:ring-2 focus:ring-blue-500"
            >
                Создать статус
            </button>
            <!-- Cancel Link -->
            <a
                    href="/statuses"
                    class="block text-center text-sm text-gray-600 hover:text-gray-900"
            >
                Отмена
            </a>
        </form>
    </div>
`
)