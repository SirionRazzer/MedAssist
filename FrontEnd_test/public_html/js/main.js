/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$("#addFormDialog .submitDialog").on('click', function () {
    var formName = $("#formName").val();
    createNewForm(formName);
    $("#addFormDialog").modal('hide');
});

$("#logXML").on('click', function () {
    logXML();
});
