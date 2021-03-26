//author: rootmain
//email:info@evercom.ru
//date:2021-03-25
//Назначение: создает задачи чеклиста из шаблона по соответствию типа текущей задачи, указанному на карточке шаблона чеклиста
//Скрипт действия по событию
//Версия системы: Naumen Service Desk 4.11

//Получаем тип текущей задачи
def objectMC = api.metainfo.getMetaClass(subject).fqnCase;
//Ищем шаблон чеклиста, в атрибуте которого указан тип текущей задачи
def templateLists = utils.find('template$checkListTemp', ['removed':false]).findAll{ objectMC in it.taskMC.code.flatten()}
//если найден шаблон чеклиста, то для каждого шаблона задачи чеклиста создаем задачу и связываем ее с текущим объектом
if (templateList) {
  def templateList = templateLists[0]
  for (chkListTskTemp in templateList.chkListTskTemp) {
    if (!chkListTskTemp.removed) {
    	//собираем коллекцию атрибутов
      def attrs = [:];
      attrs.put('description', chkListTskTemp.title);
      attrs.put('orderNumber', chkListTskTemp.orderNumb);
      attrs.put('templateTask', chkListTskTemp);
      attrs.put('task', subject);
      создаем объект
      utils.create('task$checkTask', attrs);
    }
  }
  //Записываем в историю событий
  utils.event(subject, "Создан список чеклиста задачи");
  
}
