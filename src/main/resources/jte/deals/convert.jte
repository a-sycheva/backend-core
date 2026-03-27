@import ru.mentee.power.crm.model.Lead

@param Lead lead

@template.layout.main(
content = @`
    <div class="max-w-md mx-auto mt-8">
        <h1 class="text-2xl font-bold mb-6">Конвертировать лида в сделку</h1>

        <form action="/deals/convert" method="post" class="space-y-4">
            <div>
                <input type="hidden" name="leadId" value="${String.valueOf(lead.id())}">
            </div>
            <div>
                <label for="amount" class="block text-sm font-medium text-gray-700 mb-1">
                    Amount
                </label>
                <input
                        type="number"
                        id="amount"
                        name="amount"
                        required
                        class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                />
            </div>
            <!-- Submit Button -->
            <button
                    type="submit"
                    class="w-full bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600 focus:ring-2 focus:ring-blue-500"
            >
                Конвертировать в сделку
            </button>
            <!-- Cancel Link -->
            <a
                    href="/deals"
                    class="block text-center text-sm text-gray-600 hover:text-gray-900"
            >
                Отмена
            </a>
        </form>
    </div>
`
)