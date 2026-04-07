@import org.springframework.validation.BindingResult
@import ru.mentee.power.crm.model.Company


@param Company company
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
        <label for="name">Company</label>
        <input type="text" name="name" value="${company.getName()}"
               class="${errors.hasFieldErrors("name") ? "border-red-500" : "border-gray-300"}">
        @if(errors.hasFieldErrors("name"))
            <p class="text-red-600 text-sm mt-1">${errors.getFieldError("name").getDefaultMessage()}</p>
        @endif
    </div>
    <div class="mb-4">
        <label for="industry">Email</label>
        <input type="text" name="industry" value="${company.getIndustry()}"
               class="${errors.hasFieldErrors("industry") ? "border-red-500" : "border-gray-300"}">
        @if(errors.hasFieldErrors("industry"))
            <p class="text-red-600 text-sm mt-1">${errors.getFieldError("industry").getDefaultMessage()}</p>
        @endif
    </div>
`
)