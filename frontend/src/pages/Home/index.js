import { React, useState } from 'react'
import { useHistory } from 'react-router-dom';
import { FaSignOutAlt, FaUserEdit, FaMarker, FaHome, FaRegClipboard } from 'react-icons/fa'
import { Container, Row, Col, Nav } from 'react-bootstrap'

import Header from './../../components/Header';
import Cadastro from './../Cadastro';
import Perfil from './../Perfil';
import Codelist from './../Codelist';


import "./index.css"



function Home(){
    const [conteudo, setConteudo] = useState();
    const renderCadastro = () =>(setConteudo(<Cadastro />))
    const renderPerfil = () =>(setConteudo(<Perfil />))
    const renderCodelist = () =>(setConteudo(<Codelist />))


    const hist = useHistory();

    const logout = () =>((
        localStorage.removeItem("token"),
        localStorage.removeItem("manterConectado"),
        hist.push("/")
    ))
    return (
        <Container id="home" fluid>
            <Row id="row-header">
                <Header id="header" />
            </Row>
            <Row id="row-body">
                <Col xs={2} id="col-navbar" >
                    <Nav defaultActiveKey="/home" id="nav-menu" className="flex-column">
                        <Nav.Link href="/home"><FaHome /> <span>Home</span></Nav.Link>
                        <Nav.Link onClick={renderCadastro}><FaMarker /><span>Cadastro</span></Nav.Link>
                        <Nav.Link onClick={renderPerfil}><FaUserEdit /><span>Perfil</span></Nav.Link>
                        <Nav.Link onClick={renderCodelist}><FaRegClipboard /><span>Codelist</span></Nav.Link>
                        <Nav.Link onClick={logout}><FaSignOutAlt /><span>Logout</span></Nav.Link>
                    </Nav>
                </Col>
                <Col id="col-body">
                    {conteudo}
                </Col>
            </Row>
        </Container>
    )
}

export default Home;
