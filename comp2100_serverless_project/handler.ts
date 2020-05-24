import { Handler } from 'aws-lambda';
import { APIGatewayProxyHandler } from 'aws-lambda';
import { DynamoDB } from 'aws-sdk';

const dynamoDB = new DynamoDB.DocumentClient({region: 'ap-southeast-2'});

const validInput = (data) =>{
  if (typeof data['user_id'] != "string" 
    || typeof data['username'] != "string" 
    || typeof data['scrap'] != "number" 
    || typeof data['food'] != "number" 
    || typeof data['toilet_paper'] != "number"
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
      user_id: data['user_id']
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
      user_id: data['user_id'],
      username: data['username'],
      scrap: data['scrap'],
      food: data['food'],
      toilet_paper: data['toilet_paper'],
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
      user_id: data['user_id']
      }
  };

  //Check if user already exists
  try {
    const result = await dynamoDB.get(fetchParams).promise();
    if (result.Item == null){
      return {
        statusCode: 400,
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
      user_id: data['user_id'],
      username: data['username'],
      scrap: data['scrap'],
      food: data['food'],
      toilet_paper: data['toilet_paper'],
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

  const userId = event.headers['user_id'];

  //Check if input data is valid
  if (typeof userId != "string"){
    return {
      statusCode: 400,
      body: 'Malformed data.',
    }
  }

  const fetchParams = {
    TableName: process.env.DYNAMODB_TABLE,
    Key : {
      user_id: userId
      }
  };

  //Fetch user information
  try {
    const result = await dynamoDB.get(fetchParams).promise();
    if (result.Item == null){
      return {
        statusCode: 400,
        body: 'That user does not exist.',
      }
    }
    return {
      statusCode: 200,
      body:  JSON.stringify({
        result: result.Item,
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

