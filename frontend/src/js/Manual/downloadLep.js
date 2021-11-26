import api from "./../../services/api";

export default async function downloadLep(codManual,codRevisao,traco){
    let token = localStorage.getItem("token")
    let config = {
        headers: {'Authorization': `Bearer ${token}`},
        responseType: 'blob'
    }
    return await api.get(`/manual/${codManual}/${traco}/${codRevisao}/lep`,config)
    .then((res)=>{
        return res
    })
    .catch((err)=> {
        return err
    })
};