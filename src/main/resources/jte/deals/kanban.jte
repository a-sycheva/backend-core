@import ru.mentee.power.crm.model.Deal
@import ru.mentee.power.crm.model.DealStatus
@param java.util.Map<DealStatus, java.util.List<Deal>> dealsByStatus

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Воронка продаж</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 p-8">
<h1 class="text-3xl font-bold mb-6">Воронка продаж (Kanban)</h1>

<div class="grid grid-cols-6 gap-4">
    @for(DealStatus status : DealStatus.values())
        <div class="bg-white rounded shadow-md p-4">
            <h2 class="font-bold mb-4 text-center">${status}</h2>

            @for(Deal deal : dealsByStatus.getOrDefault(status, java.util.List.of()))
                <div class="bg-gray-50 p-3 mb-2 rounded border">
                    <p class="font-semibold">${deal.getAmount()} ₽</p>
                    <p class="text-sm text-gray-600">Lead: ${deal.getLeadId().toString()}</p>

                    <!-- Форма для перехода статуса -->
                    <form action="/deals/${deal.getId().toString()}/transition" method="post" class="space-y-4">
                        <div>
                            <input type="hidden" name="leadId" value="${String.valueOf(deal.getId())}">
                        </div>
                        <div>
                            <label for="newStatus" class="block text-sm font-medium text-gray-700 mb-1">
                                Сменить статус на:
                            </label>
                            <select name="newStatus" class="w-full">
                                <option value="QUALIFIED">QUALIFIED</option>
                                <option value="PROPOSAL_SENT">PROPOSAL_SENT</option>
                                <option value="NEGOTIATION">NEGOTIATION</option>
                                <option value="WON">WON</option>
                                <option value="LOST">LOST</option>
                            </select>
                        </div>
                        <!-- Submit Button -->
                        <button
                                type="submit"
                                class="w-full bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600 focus:ring-2 focus:ring-blue-500"
                        >
                            Ок
                        </button>
                    </form>
                </div>
            @endfor
        </div>
    @endfor
</div>
</body>
</html>