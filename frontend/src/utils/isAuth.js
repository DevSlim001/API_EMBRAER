import api from './../services/api'
const isAuth = () =>{
    let token = localStorage.getItem("token")
    if(token){
        let data = {token:token}
        return api.post("/usuarios/valid",data).then((res)=>{
            return true
        }).catch((err)=>{
            localStorage.removeItem("token")
            localStorage.removeItem("manterConectado")
            window.location.href = "/"
        })
    } else{
        return false
    }
}
export default isAuth;