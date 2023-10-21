import React, { useState, useEffect } from "react";
import axios from "axios";
import "./App.css";

function App() {
  const [fields, setFields] = useState([""]);
  const [file, setFile] = useState(null);
  const [responseMessage, setResponseMessage] = useState("");
  const [tableName, setTableName] = useState("");
  const [fieldNames, setFieldNames] = useState([]);

  const [tableData, setTableData] = useState([]);

  const fetchDynamicTableData = async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/dynamic-entities/getDynamicTableData",
        {
          params: {
            table_name: tableName,
          },
        }
      );
      setTableData(response.data.data);
      console.log(fieldNames);
    } catch (error) {
      console.error("Error fetching dynamic table data:", error);
    }
  };

  const addField = () => {
    setFields([...fields, ""]);
  };

  const removeField = (index) => {
    const updatedFields = [...fields];
    updatedFields.splice(index, 1);
    setFields(updatedFields);
  };

  const handleFileUpload = (e) => {
    setFile(e.target.files[0]);
  };

  useEffect(() => {
    setFieldNames([...fields]);
  }, [fields]);

  const generateExcelTemplate = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post(
        "http://localhost:8080/generateExcelTemplate",
        fields,
        {
          responseType: "blob",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      const blob = new Blob([response.data], {
        type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });
      const link = document.createElement("a");
      link.href = window.URL.createObjectURL(blob);
      link.download = "template.xlsx";
      link.click();
    } catch (error) {
      console.error("Error generating Excel template:", error);
    }
  };

  const handleFileUploadSubmit = async (e) => {
    e.preventDefault();
    const formData = new FormData();
    formData.append("file", file);

    try {
      const response = await axios.post(
        "http://localhost:8080/dynamic-entities/upload",
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );
      setResponseMessage(response.data);
    } catch (error) {
      console.error("Error uploading file:", error);
    }
  };

  const generateTable = async (e) => {
    e.preventDefault();
    try {
      await axios.post(
        "http://localhost:8080/dynamic-entities/createPivotedTable",
        null,
        {
          params: {
            table_name: tableName,
          },
        }
      );
      setResponseMessage(`Table '${tableName}' generated successfully.`);
    } catch (error) {
      console.error("Error generating table:", error);
    }
  };

  const printTable = async (e) => {
    e.preventDefault();
    await fetchDynamicTableData();
  };

  return (
    <div className="App">
      <h1>Templatization of Orgs and Login</h1>
      <form onSubmit={generateExcelTemplate}>
        {fields.map((field, index) => (
          <div key={index} className="form-field">
            <input
              type="text"
              value={field}
              onChange={(e) => {
                const updatedFields = [...fields];
                updatedFields[index] = e.target.value;
                setFields(updatedFields);
              }}
              placeholder={`Field ${index + 1}`}
            />
            <button type="button" onClick={() => removeField(index)}>
              Remove
            </button>
          </div>
        ))}
        <button type="button" onClick={addField}>
          Add Field
        </button>
        <button type="submit">Generate Excel Template</button>
      </form>
      <br></br>
      <form onSubmit={handleFileUploadSubmit}>
        <input type="file" onChange={handleFileUpload} />
        <button type="submit">Upload File</button>
      </form>
      <br></br>
      <form onSubmit={generateTable}>
        <input
          type="text"
          value={tableName}
          onChange={(e) => setTableName(e.target.value)}
          placeholder="Enter Table Name"
        />
        <button type="submit">Generate Table</button>
      </form>
      <button onClick={printTable}>Print Table</button>
      {responseMessage && <div>{responseMessage}</div>}
      <div className="table-container">
        <h2>Dynamic Table Data</h2>
        <table className="custom-table">
          <thead>
            <tr>
              {fieldNames.map((field, index) => (
                <th key={index}>{field}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {tableData.map((row, index) => (
              <tr key={index}>
                {Object.values(row).map((value, index) => (
                  <td key={index}>{value}</td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default App;
