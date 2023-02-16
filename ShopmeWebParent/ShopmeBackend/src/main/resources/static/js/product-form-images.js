$(document).ready(function() {

    $("#fileImage").on("change", function() {
        let fileSize = this.files[0].size;

        if(fileSize > 1048576) {
            this.setCustomValidity("File image must be less than 1MB!");
            this.reportValidity();
            $("#thumbnail").attr("src", defaultImageThumbnail);
        } else {
            showThumbnail(this);
        }
    });

    $("input[name='fileExtraImage']").each(function(index) {
        $(this).on("change", function() {
            checkExtraFileImageAndShowThumbnail(this, index + 1);
        });
    });

    // $(".extraImageHeader").each(function(index) {
    //     let html = `
    //                 <a class="fas fa-times-circle fa-2x float-right icon-dark" style="cursor: pointer"
    //                                     onClick="removeExtraImage(${index + 1})"></a>
    //                 `;
    //     this.append(html);
    // });

});

// Function 1
function showThumbnail(fileInput) {
    let file = fileInput.files[0];
    let reader = new FileReader();
    reader.onload = function(e) {
        $("#thumbnail").attr("src", e.target.result);
    }
    reader.readAsDataURL(file);
}

// Function 2
function checkExtraFileImageAndShowThumbnail(extraFileImage, order) {
    let fileSize = extraFileImage.files[0].size;

    if(fileSize > 1048576) {
        extraFileImage.setCustomValidity("File image must be less than 1MB!");
        extraFileImage.reportValidity();
        $("#extraThumbnail" + order).attr("src", defaultImageThumbnail);
    } else {
        showExtraThumbnail(extraFileImage, order); // function 2.1
        if(order == totalExtraImages) {
            addNewExtraImageDiv(++order); // function 2.2
        }
    }
}

// Function 2.1
function showExtraThumbnail(fileInput, order) {
    let file = fileInput.files[0];
    let reader = new FileReader();
    reader.onload = function(e) {
        $("#extraThumbnail" + order).attr("src", e.target.result);
    }
    reader.readAsDataURL(file);

    // Change hidden input value when user change extra image:
    let fileName = file.name;
    let hiddenExtraImageName = $("#imageName" + order);
    hiddenExtraImageName.val(fileName);
}

// Function 2.2
function addNewExtraImageDiv(order) {
    ++totalExtraImages;
    let singleExtraImageDivHtml = `
        <div id="extraImage${order}" class="border border-secondary rounded p-3 extraImage">
            <div id="extraImageHeader${order}">
                <label class="font-weight-bold">Extra image #${order}:</label>
            </div>
            <div class="my-3">
                <img id="extraThumbnail${order}" class="img-fluid extra-thumbnail" src="${defaultImageThumbnail}">
            </div>
            <div>
                <input type="file" name="fileExtraImage" accept="image/png, image/jpeg"
                onchange="checkExtraFileImageAndShowThumbnail(this, ${order})">
            </div>
        </div>
    `;

    let removeBtn = `
        <a class="fas fa-times-circle fa-2x float-right icon-dark" style="cursor: pointer"
        onclick="removeExtraImage(${order - 1})"></a>
    `;

    $("#extraImageDiv").append(singleExtraImageDivHtml);
    $("#extraImageHeader" + (order - 1)).append(removeBtn);
}

function removeExtraImage(order) {
    $("#extraImage" + order).remove();
}