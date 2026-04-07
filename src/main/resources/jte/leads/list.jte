@import java.util.List

@import ru.mentee.power.crm.model.Lead
@import ru.mentee.power.crm.model.LeadStatus

@param List<Lead> leads
@param LeadStatus status
@param String name
@param String email
@param String companyName

@template.layout.main(content = @`
    <div class="bg-white rounded-lg shadow-md p-6">
        <h2 class="text-2xl font-bold mb-4">Lead List</h2>

        <div>
            <a
                    href="/leads/new"
                    class="bg-green-500 text-white px-2 py-1 rounded hover:bg-green-600"
            >
                + Добавить лида
            </a>
        </div>
        <br>

        <div class="mb-4 flex gap-2 justify-between items-center">
            <form action="/leads" method="get" class="px-4 py-2 rounded bg-blue-500">
                <label for="name">
                    Name:
                    <input type="text" name="name" value="${name}">
                </label>
                <label for="email">
                    Email:
                    <input type="text" name="email" value="${email}">
                </label>
                <label for="company">
                    Company:
                    <input type="text" name="companyName" value="${companyName}">
                </label>
                <label for="status">
                    Status
                    <select name="status">
                        @if(status == null)
                            <option value="" selected></option>
                        @else
                            <option value=""></option>
                        @endif

                        @if(status == LeadStatus.NEW)
                            <option value="NEW" selected>Новый</option>
                        @else
                            <option value="NEW">Новый</option>
                        @endif

                        @if(status == LeadStatus.CONTACTED)
                            <option value="CONTACTED" selected>Связались</option>
                        @else
                            <option value="CONTACTED">Связались</option>
                        @endif

                        @if(status == LeadStatus.QUALIFIED)
                            <option value="QUALIFIED" selected>Квалифицированный</option>
                        @else
                            <option value="QUALIFIED">Квалифицированный</option>
                        @endif
                    </select>
                </label>
                <button type="submit"
                        class="bg-white rounded px-1 py-1 hover:bg-gray-200"
                >
                    Поиск
                </button>
            </form>
        </div>
        <table class="min-w-full bg-white border border-gray-200">
            <thead class="bg-gray-100">
            <tr>
                <th class="px-4 py-2 text-left">Name</th>
                <th class="px-4 py-2 text-left">Email</th>
                <th class="px-4 py-2 text-left">Company</th>
                <th class="px-4 py-2 text-left">Status</th>
                <th class="px-4 py-2 text-left"></th>
                <th class="px-4 py-2 text-left"></th>
            </tr>
            </thead>
            <tbody>
            @for(var lead : leads)
                <tr class="border-t hover:bg-gray-50">
                    <td class="px-4 py-2">${lead.name()}</td>
                    <td class="px-4 py-2">${lead.email()}</td>
                    <td class="px-4 py-2">
                        ${lead.company() != null ? lead.company().getName() : "не указана"}
                    </td>
                    <td class="px-4 py-2">
                            <span class="px-2 py-1 rounded text-sm bg-green-100 text-green-800">
                                ${lead.status()}
                            </span>
                    </td>
                    <td>
                        <a
                                href="/leads/${String.valueOf(lead.id())}/edit"
                                class="text-blue-600 hover:underline"
                        >
                            Редактировать
                        </a>
                    </td>
                    <td>
                        <form action="/leads/${lead.id().toString()}/delete"
                              method="post" class="inline"
                              onclick="return confirm('Удалить лида c e-mail: ${lead.email()}?')"
                        >
                            <button
                                    type="submit"
                                    class="bg-red-600 text-white px-2 rounded hover:bg-red-700"
                            >
                                Удалить
                            </button>
                        </form>
                    </td>
                </tr>
            @endfor
            </tbody>
        </table>
    </div>
`)