@import java.util.List

@import ru.mentee.power.crm.model.Lead
@import ru.mentee.power.crm.model.LeadStatus

@param List<Lead> leads
@param LeadStatus status
@param String email
@param String company

@param  List<String> statuses

@template.layout.main(content = @`
    <div class="bg-white rounded-lg shadow-md p-6">
        <h2 class="text-2xl font-bold mb-4">Status List</h2>

        <div>
            <a
                    href="/statuses/new"
                    class="bg-green-500 text-white px-2 py-1 rounded hover:bg-green-600"
            >
                + Добавить статус
            </a>
        </div>
        <br>
        <table class="min-w-full bg-white border border-gray-200">
            <thead class="bg-gray-100">
            <tr>
                <th class="px-4 py-2 text-left">Status</th>
            </tr>
            </thead>
            <tbody>
            @for(var status : statuses)
                <tr class="border-t hover:bg-gray-50">
                    <td class="px-4 py-2">${status}</td>
                </tr>
            @endfor
            </tbody>
        </table>
    </div>
`)