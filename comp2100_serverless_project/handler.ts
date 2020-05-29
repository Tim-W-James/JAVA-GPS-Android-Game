import { Handler } from 'aws-lambda';
import { APIGatewayProxyHandler } from 'aws-lambda';
import { DynamoDB } from 'aws-sdk';

const dynamoDB = new DynamoDB.DocumentClient({region: 'ap-southeast-2'});

const validInput = (data) =>{
  if (typeof data['id'] != "string" 
    || typeof data['displayName'] != "string" 
    || typeof data['currentInventory']['food'] != "number" 
    || typeof data['currentInventory']['scrapMetal'] != "number" 
    || typeof data['currentInventory']['toiletPaper'] != "number"
    || typeof data['currentInventory']['value'] != "number"
  ){
    return false;
  }
  return true;
}

const authenticate = (auth: string) => {
  if(auth == null) {
    return false;
  }

  var tmp = auth.split(' ');
  var buffer = Buffer.from(tmp[1], 'base64');
  var plain_auth = buffer.toString();

  var creds = plain_auth.split(':');
  var username = creds[0];
  var password = creds[1];

  console.log(username);
  console.log(password);

  //These should be encrypted and stored as environment variables ¯\_(ツ)_/¯ 
  if (username != "comp2100BunkerAdmin" || password != 'zvQzzetkP2vr45HR'){
    return false;
  }
  return true;
}

export const createUser: Handler = async (event, _context) => {
  const auth = event.headers['Authorization'];

  if (!authenticate(auth)){
    return {
      statusCode: 401,
      body: 'Bad authentication.',
    }
  }

  const data =  JSON.parse(event.body);

  //Check if input data is valid
  if (!validInput(data)){
    return {
      statusCode: 400,
      body: 'Malformed data.',
    }
  }

  const fetchParams = {
    TableName: process.env.DYNAMODB_TABLE,
    Key : {
      id: data['id']
      }
  };

  //Check if user already exists
  try {
    const result = await dynamoDB.get(fetchParams).promise();
    if (result.Item != null){
      return {
        statusCode: 400,
        body: 'That user already exists.',
      }
    }
  } catch (e){
    console.log(e.message)
    return {
      statusCode: 500,
      body: 'Couldn\'t connect to the database.',
    }
  }

  const params = {
    TableName: process.env.DYNAMODB_TABLE,
    Item: {
      id: data['id'],
      displayName: data['displayName'],
      currentInventory: {
        food: data['currentInventory']['food'],
        scrapMetal: data['currentInventory']['scrapMetal'],
        toiletPaper: data['currentInventory']['toiletPaper'],
        uniqueItems: data['currentInventory']['uniqueItems'],
        value: data['currentInventory']['value'],
      },
      value: data['currentInventory']['value'],
      GSI: "ok"
    },
  };

  //Attempt to create database entry
  try {
    await dynamoDB.put(params).promise()
    return {
      statusCode: 200,
      body: JSON.stringify(params.Item),
    }
  } catch (e) {
    console.log(e.message)
    return {
      statusCode: 500,
      body: 'Couldn\'t create the user item.',
    }
  }
}

export const modifyUser: Handler = async (event, _context) => {
  const auth = event.headers['Authorization'];
  
  if (!authenticate(auth)){
    return {
      statusCode: 401,
      body: 'Bad authentication.',
    }
  }
  const data =  JSON.parse(event.body);

  //Check if input data is valid
  if (!validInput(data)){
    return {
      statusCode: 400,
      body: 'Malformed data.',
    }
  }

  const fetchParams = {
    TableName: process.env.DYNAMODB_TABLE,
    Key : {
      id: data['id']
      }
  };

  //Check if user already exists
  try {
    const result = await dynamoDB.get(fetchParams).promise();
    if (result.Item == null){
      return {
        statusCode: 404,
        body: 'That user does not exist.',
      }
    }
  } catch (e){
    console.log(e.message)
    return {
      statusCode: 500,
      body: 'Couldn\'t connect to the database.',
    }
  }

  const params = {
    TableName: process.env.DYNAMODB_TABLE,
    Item: {
      id: data['id'],
      displayName: data['displayName'],
      currentInventory: {
        food: data['currentInventory']['food'],
        scrapMetal: data['currentInventory']['scrapMetal'],
        toiletPaper: data['currentInventory']['toiletPaper'],
        uniqueItems: data['currentInventory']['uniqueItems'],
        value: data['currentInventory']['value'],
      },
      value: data['currentInventory']['value'],
      GSI: "ok"
    },
  };

  //Attempt to create database entry
  try {
    await dynamoDB.put(params).promise()
    return {
      statusCode: 200,
      body: JSON.stringify(params.Item),
    }
  } catch (e) {
    console.log(e.message)
    return {
      statusCode: 500,
      body: 'Couldn\'t create the user item.',
    }
  }
}

export const getUserInfo: APIGatewayProxyHandler = async (event, _context) => {

  const auth = event.headers['Authorization'];
  if (!authenticate(auth)){
    return {
      statusCode: 401,
      body: 'Bad authentication.',
    }
  }

  const id = event.headers['id'];

  //Check if input data is valid
  if (typeof id != "string"){
    return {
      statusCode: 400,
      body: 'Malformed data.',
    }
  }

  const fetchParams = {
    TableName: process.env.DYNAMODB_TABLE,
    KeyConditionExpression: 'id = :id',
    ExpressionAttributeValues: {
      ':id': id
    }
  };

  //Fetch user information
  try {
    const result = await dynamoDB.query(fetchParams).promise();
    if (result.Items[0] == null){
      return {
        statusCode: 404,
        body: 'That user does not exist.',
      }
    }
    return {
      statusCode: 200,
      body:  JSON.stringify({
        result: result.Items[0],
      }, null, 2),
    }
  } catch (e){
    console.log(e.message)
    return {
      statusCode: 500,
      body: 'Couldn\'t connect to the database.',
    }
  }
}

export const getTopFive: APIGatewayProxyHandler = async (event, _context) => {

  const auth = event.headers['Authorization'];
  if (!authenticate(auth)){
    return {
      statusCode: 401,
      body: 'Bad authentication.',
    }
  }

  const fetchParams = {
    TableName: process.env.DYNAMODB_TABLE,
    IndexName: "myGSI",
    Limit: 5,
    KeyConditionExpression: "GSI = :GSI",
    ExpressionAttributeValues: {
      ":GSI": "ok"
  },
    ScanIndexForward: false
  };

  //Fetch user information
  try {
    const result = await dynamoDB.query(fetchParams).promise();
    if (result.Items == null){ 
      return {
        statusCode: 400,
        body: 'No users exist!',
      }
    }
    return {
      statusCode: 200,
      body:  JSON.stringify({
        result: result.Items,
      }, null, 2),
    }
  } catch (e){
    console.log(e.message)
    return {
      statusCode: 500,
      body: 'Couldn\'t connect to the database.',
    }
  }
}

