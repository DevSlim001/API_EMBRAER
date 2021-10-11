import './index.css';
import { React, useState, useEffect } from 'react'
import { Button, Modal, Form, Spinner, Table, Row, Col } from 'react-bootstrap'
import getManuals from './../../js/Manual/getManuals'
import createManual from './../../js/Manual/createManual'
import getManualById from './../../js/Manual/getManualById'
import importCodelist from './../../js/Manual/importCodelist'
import createCodelist from './../../js/Manual/createCodelist'
import deleteBloco from './../../js/Manual/deleteBloco'

import { FaTrashAlt } from 'react-icons/fa';



import FormCreateCodelist from './../../components/FormCreateCodelist';




import $ from 'jquery';

function Codelist() {

    const [manuais, setManuais] = useState([])
    const [manualTable, setManualTable] = useState()

    const [showSpinnerCadastroManual, setShowSpinnerCadastroManual] = useState(false)
    const [showModalCadastroManual, setShowModalCadastroManual] = useState(false)
    const [msgModalCadastroManual, setMsgModalCadastroManual] = useState(false)
    const [showTableCodelist, setShowTableCodelist] = useState(false)
    const [showFormArquivoCodelist, setShowFormArquivoCodelist] = useState(false)
    const [showBtnCreateCodelist, setShowBtnCreateCodelist] = useState(false)

    const handleCloseModalCadastro = () => setShowModalCadastroManual(false);
    const handleShowModalCadastro = () => setShowModalCadastroManual(true);
    const [formCreateCodelist, setFormCreateCodelist] = useState([])
    const addBlocoForm = () => {
        let formList = [<FormCreateCodelist />,...formCreateCodelist]
        setFormCreateCodelist(formList)
    }
    const removeBloco = (codBloco) => {
        let codManual = $("#form-manuais").serializeArray()[0].value
        deleteBloco(codManual,codBloco).then((res) => {
            if(res.status !== 200){

            } else {
                setShowFormArquivoCodelist(false)
                renderTable(res.data)
            }
        })
    }
    const handleCreateCodelist = () => {
        let formList = $(".form-create-codelist")
        let codelist = []
        let codManual = $("#form-manuais").serializeArray()[0].value
        formList.each((index,f) => {
            let form = $(f)
            let formData = {}
            $.each(form.serializeArray(), function(i, field) {
                if(field.name==="tracos"){
                    formData[field.name] = field.value.split(",");

                } else {
                    formData[field.name] = field.value;
                }
            });
            if(formData.codBlocoCodelist || formData.nomeBloco || formData.numBloco || formData.numSecao || !formData.tracos.lenght===0){
                codelist.push(formData)
            }        
        })
        createCodelist(codelist,codManual).then((res) => {
            if(res.status!==201){

            } else {
                setShowFormArquivoCodelist(false)
                renderTable(res.data)
            }
        })

    }
    const handleImportCodelist = (arquivo) => {
        let form = $("#form-manuais")
        let codManual = form.serializeArray()[0].value
        importCodelist(arquivo, codManual).then((res) => {
            if(res.status!==201){

            } else {
                setShowFormArquivoCodelist(false)
                renderTable(res.data)
            }

        })

    }
    const handleCreateManual = () => {
        setShowSpinnerCadastroManual(true)

        let form = $("#form-cadastro-manual").serializeArray()
        let btnCadastro = $("#btn-cadastro-manual")
        btnCadastro.attr("disabled", true)
        let manual = {}
        $.each(form, function (i, field) {
            manual[field.name] = field.value;
        });
        createManual(manual).then((res) => {
            if (res.status !==201) {
                setShowSpinnerCadastroManual(false)
                setMsgModalCadastroManual(`✓ Cadastro realizado com sucesso.`)
                btnCadastro.attr("disabled", false)
                loadManuals()
            } else {
                setMsgModalCadastroManual(`✗ ${res.data.errors[0]}`)
            }
        }).catch((err) => {
            console.log(err)
        })

    }

    const handleManualForm = (e) => {
        e.preventDefault()
        setShowTableCodelist(false)
        setShowFormArquivoCodelist(false)
        let form = $("#form-manuais")
        let opt = form.serializeArray()[0].value
        if (opt === "cadastro") {
            handleShowModalCadastro(true)
        } else {
            getManualById(opt).then((res) => {
                if (res.data.secoes.length > 0) {
                    renderTable(res.data)
                } else {
                    setShowFormArquivoCodelist(true)
                }
            })

        }
    }
    const renderTable = (manualRes) => {
        let manual = []
        manualRes.secoes.forEach((secao, index) => {
            secao.blocos.forEach((bloco, index) => {
                let traco = ""
                bloco.tracos.forEach((tracoRes, index) => {
                    traco += tracoRes.traco + ","
                })
                traco = traco.substring(0, traco.length - 1)
                let data = {
                    numSecao: secao.numSecao,
                    numSubSecao: "",
                    numBloco: bloco.numBloco,
                    nomeBloco: bloco.nomeBloco,
                    codBloco:bloco.codBloco,
                    codBlocoCodelist: bloco.codBlocoCodelist,
                    traco: traco
                }
                manual.push(data)

            })
            secao.subSecoes.forEach((subSecao, index) => {
                subSecao.blocos.forEach((bloco, index) => {

                    let traco = ""
                    bloco.tracos.forEach((tracoRes, index) => {
                        traco = traco + tracoRes.traco + ","
                    })
                    traco = traco.substring(0, traco.length - 1)
                    let data = {
                        numSecao: secao.numSecao,
                        numSubSecao: subSecao.numSubSecao,
                        numBloco: bloco.numBloco,
                        nomeBloco: bloco.nomeBloco,
                        codBloco:bloco.codBloco,
                        codBlocoCodelist: bloco.codBlocoCodelist,
                        traco: traco
                    }
                    manual.push(data)
                })
            })
        })
        setManualTable(manual)
        setShowTableCodelist(true)
    }

    const loadManuals = () => {
        getManuals().then((res) => {
            setManuais(res.data)
        }).catch((err) => {
            console.log(err)
        })
    }

    useEffect(() => {
        loadManuals()
        addBlocoForm()
    }, [])


    return (
        <div id="form-div-manual">
            <Form id="form-manuais">
                <Form.Select aria-label="" defaultValue="" onInput={handleManualForm} name="manual" required>
                    <option value="" disabled >Selecione o manual</option>
                    <option value="cadastro">Criar novo manual +</option>
                    {manuais.map((manual, index) => (
                        <option key={manual.codManual} value={manual.codManual}>{manual.nome}-{manual.partNumber}</option>
                    ))}
                </Form.Select>
            </Form>
            <br />
            {showFormArquivoCodelist &&
                <div>
                    <br />
                    <Form id="form-arquivo-codelist" encType="multipart/form-data">
                        <Form.Group controlId="arquivo-codelist" className="mb-3">
                            <center>
                                <Form.Label>
                                    <h4>Faça upload de um arquivo</h4>
                                </Form.Label>
                            </center>
                            <br />
                            <Form.Control onChange={(e) => handleImportCodelist(e.target.files[0])} name="arquivo" type="file" accept=".xlsx" />
                        </Form.Group>
                    </Form>
                    <br />
                    <center>
                        <h4>Ou crie um codelist com o formulário abaixo:</h4>
                    </center>
                    <br />
                    {formCreateCodelist}
                    <div className="d-grid gap-2">
                        <Button variant="primary" onClick={addBlocoForm} >
                          Adicionar Bloco
                        </Button>
                        <Button variant="primary" onClick={handleCreateCodelist}>
                            Cadastrar Codelist
                        </Button>
                    </div>
                </div>
            }
            {showTableCodelist &&
                <Table responsive striped bordered hover variant="light">
                    <thead>
                        <tr>
                            <th>Nº Seção</th>
                            <th>Nº Sub Seção</th>
                            <th>Nº Bloco</th>
                            <th>Nome do bloco</th>
                            <th>Código</th>
                            <th>Traços</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        {manualTable.map((linha, index) => (
                            <tr>
                                <td>{linha.numSecao}</td>
                                <td>{linha.numSubSecao}</td>
                                <td>{linha.numBloco}</td>
                                <td>{linha.nomeBloco}</td>
                                <td>{linha.codBlocoCodelist}</td>
                                <td>{linha.traco}</td>
                                <td onClick={() => removeBloco(linha.codBloco)} ><FaTrashAlt /></td>
                            </tr>
                        ))}
                    </tbody>
                </Table>
            }

            <Modal show={showModalCadastroManual} onHide={handleCloseModalCadastro}>
                <Modal.Header closeButton>
                    <Modal.Title>Cadastro de manual</Modal.Title>
                </Modal.Header>
                <Modal.Body id="modal-body">
                    <Form id="form-cadastro-manual">
                        <Form.Group className="mb-3" controlId="nome">
                            <Form.Label>Nome</Form.Label>
                            <Form.Control type="text" name="nome" placeholder="ABC" required />
                        </Form.Group>
                        <Form.Group className="mb-3" controlId="partNumber">
                            <Form.Label>Nome</Form.Label>
                            <Form.Control type="text" name="partNumber" placeholder="1234" required />
                        </Form.Group>
                    </Form>
                    <span id="msgModalCadastroManual">{msgModalCadastroManual}</span>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleCloseModalCadastro}>Voltar</Button>
                    <Button type="button" id="btn-cadastro-manual" variant="primary" onClick={handleCreateManual}>
                        {showSpinnerCadastroManual &&
                            <Spinner id="spinner-form-cadastro-manual" as="span" animation="border" size="sm" role="status" aria-hidden="true" />
                        }
                        Confirmar
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
}

export default Codelist;