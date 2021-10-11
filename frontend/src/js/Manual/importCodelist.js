  
import api from './../../services/api'

async function importCodelist(arquivo,codManual){
   const formData = new FormData()
   formData.append('arquivo', arquivo)
   let token = localStorage.getItem("token")
    let config = {
        headers: {'Authorization': `Bearer ${token}`}
    }
    return await api.post(`/manual/${codManual}/codelist`,formData,config).then((res)=>{
        return res
    }).catch((err)=>{
        return err
    }) 
}

export default importCodelist;