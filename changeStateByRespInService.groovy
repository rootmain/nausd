//author: rootmain
//email:info@evercom.ru
//date:2021-04-24
/**
 * Назначение:Автоматически изменяет статус (если разрешен переход)
 * текущего объекта (запроса), указывает ответственного из связанной
 * услуги и добавляет коментарий. Иначе добавляет комментарий о причине
 * невозможности перевода в статус.
 */
//Скрипт действия перехода в статус
//Версия системы: Naumen Service Desk 4.9

//ПАРАМЕТРЫ------------------------------------------------------------

NEW_STATE = 'assigned';


currentCall = subject;
fqn = currentCall.metaClass
CALL_SERVICE = 'service';
RESP_TEAM = 'responsibleTeam';
REST_EMPL = 'responsibleEmployee';


def responsibleEmployee
def responsibleTeam
def stateTitle = api.metainfo.getStateTitle(fqn, NEW_STATE)

//ФУНКЦИ---------------------------------------------
def allowedToChangeState(def object, def newState)
{
    if(object && newState)
    {
        for(def transition : api.wf.transitions(object))
        {
            if(transition.enabled && newState.equals(transition.endState))
            {
                return true;
            }
        }
    }
    return false;
}
//ОСНОВНОЙ БЛОК------------------------------------------------
def attrs = [:];
def comment
attrs.put('@isCommentPrivate',true)
if (allowedToChangeState(currentCall, NEW_STATE)) {
  if (currentCall[CALL_SERVICE]) {
    if (service[RESP_TEAM]) {
      attrs.put('@comment', "Выполнен автоматический переход в статус \"${stateTitle.toString()}\" и назначен ответственный ${service[RESP_TEAM].title}${service[RESP_TEAM]?'/' + service[REST_EMPL].title:''} по услуге \"${service.title}.\"")
      attrs.put('state':NEW_STATE);
      attrs.put('responsibleTeam',service[RESP_TEAM]);
      attrs.put('responsibleEmployee',service[REST_EMPL]);
    }
    else {
      attrs.put('@comment', "Автоматический переход в статус \"${stateTitle.toString()}\" не выполнен, так как в услуге \"${service.title}\" не указан ответственный.")
    }
  }
  else {
    attrs.put('@comment', "Автоматический переход в статус \"${stateTitle.toString()}\" не выполнен, так как не указана услуга.")
  }
}
else {
  attrs.put('@comment', "Автоматический переход в статус ${stateTitle.toString()} не выполнен, так как переход не разрешен.")

}

utils.edit(currentCall, attrs)
