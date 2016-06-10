/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/* global currentSlide */

var questionType;
var questionId;

$(document).ready(function () {
    $("#addFormDialog .submitDialog").on('click', function () {
        var formName = $("#formName").val();
        createNewForm(formName);
        clearCurrentForm();
        $("#form-container > h3").html("Dotazník: " + formName);
    });
    $("#addSlideDialog .submitDialog").on('click', function () {
        var slideName = $("#slideName").val();
        var slideDependency = $("#slideDependency").prop("checked");
        var slide = addSlide(slideName, slideDependency);
        addSlideHTML(slideName, slide.getAttribute('sid'));
    });
    $("#addQuestionGroup a").on('click', function () {
        questionType = $(this).data('type');
    });
    $("#addRangeDialog .submitDialog").on('click', function () {
        var questionText = $("#rangeText").val();
        var min = $("#rangeMin").val();
        var max = $("#rangeMax").val();
        var step = $("#rangeStep").val();
        var question = addQuestion(currentSlide, questionType, questionText);
        setRange(question, min, max, step);
        addRangeHTML(question.getAttribute('qid'), questionText, min, max, step);
    });
    $("#addQuestionDialog .submitDialog").on('click', function () {
        var questionText = $("#questionText").val();
        var question = addQuestion(currentSlide, questionType, questionText);
        addQuestionHTML(questionType, questionText, question.getAttribute('qid'));
    });

    $("#addOptionsDialog .submitDialog").on('click', function () {
        var options = [];
        $('#addOptionsDialog input').each(function () {
            if ($(this).val() != "") {
                options.push($(this).val());
            }
        });
        setOptions(getQuestionById(questionId), options);
        addOptionsHTML(questionId, options);
    });

    $("#logXML").on('click', function () {
        logXML();
    });
    $(".modal .submitDialog").on('click', function () {
        $(this).parents('.modal').modal('hide');
    });
});

function addSlideThumbnail(slideName, sid) {
    $("#slides-thumbnails-container").append("<div class='col-sm-1 slide-thumbnail' data-sid='" + sid + "'>" + "slide " + sid + "</br>" + slideName + "</div>");
    $(".slide-thumbnail[data-sid='" + sid + "'").on('click', function () {
        changeActiveSlide(sid);
    });
}

function addSlideHTML(slideName, sid) {
    $("#slide-container").append("<div class='slide' data-sid='" + sid + "'></div>");
    addSlideThumbnail(slideName, sid);
    changeActiveSlide(sid);
    $("#slide-container .slide.active").append('<h3>slide: ' + slideName + '</h3>');
}
function addQuestionHTML(type, text, qid) {
    var questionHTML = "";
    switch (type) {
        case "textbox":
            questionHTML = "<div class='form-group' data-qid='" + qid + "'><label>" + text + "</label><input type='textbox' class='form-control'></div>";
            break;
        case "date":
            questionHTML = "<div class='form-group' data-qid='" + qid + "'><label>" + text + "</label><input type='date' class='form-control'></div>";
            break;
        case "checkbox":
            questionHTML = "<div class='form-group' data-qid='" + qid + "'><p><b>" + text + "</b></p><button class='btn btn-default addQuestionOptions' data-type='checkbox' data-qid='" + qid + "'>Přidat možnosti</button></div>";
            break;
        case "radiobutton":
            questionHTML = "<div class='form-group' data-qid='" + qid + "'><p><b>" + text + "</b></p><button class='btn btn-default addQuestionOptions' data-type='radio' data-qid='" + qid + "'>Přidat možnosti</button></div>";
            break;
    }
    $("#slide-container .slide.active").append(questionHTML);
    bindAddingOptions(qid, type);
}

function addOptionsHTML(qid, options) {
    console.log(options);
    var type = $('.form-group[data-qid="' + qid + '"] .addQuestionOptions').data('type');
    for (var i = 0; i < options.length; i++) {
        $('.form-group[data-qid="' + qid + '"]').append("<input type='" + type + "' name='" + qid + "'>" + options[i] + " <br>");
    }
    $('.form-group[data-qid="' + qid + '"] .addQuestionOptions').remove();
}

function addRangeHTML(qid, text, min, max, step) {
    var questionHTML = "<div class='form-group' qid='" + qid + "'><label>" + text + "</label><input type='range' min='" + min + "' max='" + max + "' step='" + step + "'></div>";
    $("#slide-container .slide.active").append(questionHTML);
}

function bindAddingOptions(qid, type) {
    $('.addQuestionOptions[data-qid="' + qid + '"]').on('click', function () {
        questionType = type;
        questionId = qid;
        $('#addOptionsDialog').modal('show');
    });
}

function changeActiveSlide(sid) {
    $("#slide-container .slide.active").removeClass("active");
    $("#slide-container .slide[data-sid='" + sid + "']").addClass('active');
    changeActiveThumbnail(sid);
    var slide = getSlideBySid(sid);
    setCurrentSlide(slide);
}

function changeActiveThumbnail(sid) {
    $("#slides-thumbnails-container .slide-thumbnail.active").removeClass('active');
    $("#slides-thumbnails-container .slide-thumbnail[data-sid='" + sid + "']").addClass('active');
}

function clearCurrentForm() {
    $("#form-container > h3").html("");
    $("#slide-container").html("");
    $("#slides-thumbnails-container").html("");
}
