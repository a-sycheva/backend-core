@import java.util.List

@import ru.mentee.power.crm.model.Company
@import ru.mentee.power.crm.model.Lead
@import ru.mentee.power.crm.model.LeadStatus

@param List<Company> companies
@param String name
@param String industry

@template.layout.main(content = @`
    <div class="bg-white rounded-lg shadow-md p-6">
        <h2 class="text-2xl font-bold mb-4">Company List</h2>

        <div>
            <a
                    href="/companies/new"
                    class="bg-green-500 text-white px-2 py-1 rounded hover:bg-green-600"
            >
                + Добавить компанию
            </a>
        </div>
        <br>

        <div class="mb-4 flex gap-2 justify-between items-center">
            <form action="/companies" method="get" class="px-4 py-2 rounded bg-blue-500">
                <label for="name">
                    Name:
                    <input type="text" name="name" value="${name}">
                </label>
                <label for="email">
                    Email:
                    <input type="text" name="email" value="${industry}">
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
                <th class="px-4 py-2 text-left">Industry</th>
                <th class="px-4 py-2 text-left"></th>
                <th class="px-4 py-2 text-left"></th>
            </tr>
            </thead>
            <tbody>
            @for(var company : companies)
                <tr class="border-t hover:bg-gray-50">
                    <td class="px-4 py-2">${company.getName()}</td>
                    <td class="px-4 py-2">${company.getIndustry()}</td>
                    <td>
                        <a
                                href="/companies/${String.valueOf(company.getId())}/edit"
                                class="text-blue-600 hover:underline"
                        >
                            Редактировать
                        </a>
                    </td>
                    <td>
                        <form action="/companies/${company.getId().toString()}/delete"
                              method="post" class="inline"
                              onclick="return confirm('Удалить компанию с названием : ${company.getName()}?')"
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