function addNewDetailHandle() {
    ++totalDetails;
    let order = totalDetails;

    let singleDetailDiv = `
        <div id="detail${order}" class="form-inline">
            <input type="hidden" name="detailId" value="0">
        
            <label class="m-3">Name:</label>
            <input class="form-control w-25" name="detailName" type="text">
    
            <label class="m-3">Value:</label>
            <input class="form-control w-25" name="detailValue" type="text">
        </div>
    `;

    let removeBtn = `
        <a class="fas fa-times-circle fa-2x ml-3 icon-dark" style="cursor: pointer"
            onclick="removeDetail(${order - 1})"></a>
    `;

    $("#productDetailsDiv").append(singleDetailDiv);
    $("#detail" + (order - 1)).append(removeBtn);

    // focus on last detail
    $("input[name='detailName']").last().focus();
}

function removeDetail(detailOrder) {
    $("#detail" + detailOrder).remove();
}