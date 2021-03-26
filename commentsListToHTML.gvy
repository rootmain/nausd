//author: rootmain
//email:info@evercom.ru
//date:2021-03-25
//Назначение: Создает файл с комментариями HTML Дата, Автор, Комментарий, прикрепляет его к карточке компании и ли к карточке того же объекта, возвращает ссылку
//Для выполнения в консоли
//Версия системы: Naumen Service Desk 4.9

//ПАРАМЕТРЫ-------------------------------------------------------
//Укажите идентификатор (UUID) объекта, комментарии которого нужно получить
def serviceCallUUID = 'serviceCall$2363105'

//Прикрепить к карточке компании?
def attachToCompany = true;

//ОСНОВНАЯ ЧАСТЬ-------------------------------------------------
def subject = utils.get(serviceCallUUID)
def company = utils.get('root', [:])

def attachTo = attachToCompany?company:subject;
def attrs = [:];
attrs.put('source', subject.UUID)
def comments = utils.find('comment', attrs)

def header = """<!DOCTYPE html>
<html>
 <head>
  <meta charset="utf-8">
  <title>Кнопка</title>
 </head>
 <body>
   <table border="1" cellpadding="3" cellspacing="3"><tr><td><b>Дата</b></td><td><b>Подразделение автора</b></td><td><b>Автор</b></td><td><b>Коментарий</b></td></tr>"""
def body = ""
def footer = """</table>
 </body>
</html>"""

for (comment in comments) {
  body+="<tr><td>"
  body+=utils.formatters.formatDateTime(comment.creationDate)
  body+="</td><td>"
  body+=comment.author?.parent.title?:"нет"
  body+="</td><td>"
  body+=comment.author?.title?:"Пользователь"
  body+="</td><td>"
  body+=comment.text
  body+="</td></tr>"
}

//Формируем файл
def fileContent = header+body+footer
def fileName = 'Список комментариев ' + subject?.title + '.html';
def contentType = 'text/html';
def description = fileName;
byte[] data = fileContent.toString().getBytes();
utils.attachFile(attachTo, fileName, contentType, description, data);
//Прикрепляем к карточке объекта
def files = [];
files.addAll(utils.files(attachTo).findAll{!it.relation});
files.sort{ it.creationDate.getTime() };
file = files.last();
//возвращаем ссылку
return api.web.baseUrl+'download?uuid='+file.UUID
