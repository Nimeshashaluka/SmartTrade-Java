async  function loadFeatures() {

//    console.log("ok1");
    const response = await fetch(
            "LoadFeatures",
            );

    if (response.ok) {
        const json = await response.json();

        const  categoryList = json.categoryList;
        const  modelList = json.modelList;
        const  ColorList = json.ColorList;
        const  StorageList = json.StorageList;
        const  ProductList = json.ProductList;
//        console.log(categoryList[0].name);

        loadSelect("categorySelect", categoryList, ["id", "name"]);
        loadSelect("modelSelect", modelList, ["id", "name"]);
        loadSelect("StorageSelect", StorageList, ["id", "value"]);
        loadSelect("colorSelect", ColorList, ["id", "name"]);
        loadSelect("conditionSelect", ProductList, ["id", "name"]);
    } else {
        document.getElementById("message").innerHTML = "Please try again later!";
    }
}

function loadSelect(selectTagId, list, propertyArray) {
    const selectTag = document.getElementById(selectTagId);
    list.forEach(item => {
        let optionTag = document.createElement("option");
        optionTag.value = item[propertyArray[0]];
        optionTag.innerHTML = item[propertyArray[1]];
        selectTag.appendChild(optionTag);
    });
}