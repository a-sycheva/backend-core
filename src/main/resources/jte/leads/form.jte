@import org.springframework.validation.BindingResult
@import ru.mentee.power.crm.model.Lead
@import ru.mentee.power.crm.model.LeadStatus

@param Lead lead
@param BindingResult errors

@template.layout.main(
content = @`
    @if(errors.hasGlobalErrors())
        <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            @for(var globalError : errors.getGlobalErrors())
                <p>${globalError.getDefaultMessage()}</p>
            @endfor
        </div>
    @endif
    <div class="mb-4">
        <label for="email">Email</label>
        <input type="text" name="email" value="${lead.email()}"
               class="${errors.hasFieldErrors("email") ? "border-red-500" : "border-gray-300"}">
        @if(errors.hasFieldErrors("email"))
            <p class="text-red-600 text-sm mt-1">${errors.getFieldError("email").getDefaultMessage()}</p>
        @endif
    </div>
    <div class="mb-4">
        <label for="company">Company</label>
        <input type="text" name="company" value="${lead.company()}"
               class="${errors.hasFieldErrors("company") ? "border-red-500" : "border-gray-300"}">
        @if(errors.hasFieldErrors("company"))
            <p class="text-red-600 text-sm mt-1">${errors.getFieldError("company").getDefaultMessage()}</p>
        @endif
    </div>
    <div class="mb-4">
        <label for="status">Status</label>
        <select name="status" class="${errors.hasFieldErrors("status") ? "border-red-500" : "border-gray-300"}">
            @if(lead.status() == null)
                <option value="" selected></option>
            @else
                <option value=""></option>
            @endif

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
        @if(errors.hasFieldErrors("status"))
            <p class="text-red-600 text-sm mt-1">${errors.getFieldError("status").getDefaultMessage()}</p>
        @endif
    </div>
`
)