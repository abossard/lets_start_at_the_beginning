const R = require('ramda');

const differenceTo = (num1, num2) => Math.abs(num2 - num1)
//console.log(differenceTo(42, -666))

// Function that returns a function
const createDifferenceTo = num2 => num1 => Math.abs(num2 - num1)

const differenceToZero = createDifferenceTo(0)
//console.log(differenceToZero(100))

const createStoreDataFetcher = connection => query => {
    // do something real with the connection and fetch the data
    return {result: [{data: 1}, query, connection]}
}

const extractQueryFrom = request => request !== undefined && request.query !== undefined ? request.query : "default query"

// Function that takes a function
const getResultFromRequest = (dataFetcher, request) => {
    const query = extractQueryFrom(request)
    return dataFetcher(query)
}

const dataFetcher = createStoreDataFetcher({connection:"url"})

//setup
// console.log(getResultFromRequest(dataFetcher, {query:"user:profile"}))


// Compose (RTL)
const createResultFromRequestFunction = dataFetcher => R.compose(dataFetcher, extractQueryFrom)
const newResultFromRequest = createResultFromRequestFunction(dataFetcher)
//console.log(newResultFromRequest({query: "user:settings"}))


// Currying
const differenceToCurryable = R.curry(differenceTo)
const diffTo0 = differenceToCurryable(0)
//console.log(diffTo0(-1223))


const hasMinLength = (minLength, input) => input !== null && input.length !== undefined && input.length >= minLength

const validateForMinLength = (minLength, input) => 
    hasMinLength(minLength, input) ? input 
    : {errors: [`Min Length ${minLength} not reached for input: ${input}`]}

const containsWordFromList = (wordList, input) => {
    const lowerInput = input.toLowerCase();
    return input !== null && wordList.reduce((previousResult, word) => previousResult?previousResult:lowerInput.indexOf(word)!==-1, false);
}

const validateForNastyWords = (nastyWordList, input) => 
    !containsWordFromList(nastyWordList, input) ? input
    : {errors: [`Please no nasty words!`]}

const applyValidationConfig = (inputFormData, config) => {
    const input = inputFormData[config.field]
    const validationResults = config.validators.map(validator=>validator(input))
    const errors = validationResults.filter(result=>result.errors!==undefined)
    return {
        field: config.field,
        value: input,
        errors: errors
    }
}

const applyValidationConfigList = (validationConfigList, inputFormData) => {
    return validationConfigList.map(config=>applyValidationConfig(inputFormData, config))
}

const inputFormData = {
    "name": "Peter"
}

const validationConfigList = [
    {
        "field": "name",
        "validators": [
            (input)=>validateForMinLength(2, input),
            (input)=>validateForNastyWords(['crap'], input)
        ]
    }
]

// console.log(applyValidationConfigList(validationConfigList, {"name": "Peter"}))

// tactical approach
const validatePersonForm = formData => {
    let result = [
    ]
    let nameResult = {field: "name", value: formData.name, errors: []}
    if(formData.name!==undefined && formData.name.length < 3) {
        nameResult.errors.append("Name is too short")
    }
    const bannedWords = ['crap']
    for (let index = 0; index < bannedWords.length; index++) {
        if(formData.name.indexOf(bannedWords[index]) !== -1) {
            nameResult.errors.append("Contains banned word");
            break;
        }
    }
    result.append(nameErrors)
    return result;
}


// but in a couple weeks....

const inputFormData2 = {
    "name": "Peter",
    "surname": "Pulver",
    "city": "Zurich",
    "zip": "8000",
    "phone": "+41788823023",
    "street": "Langstrasse 12",
    "street1": ""
}

const minLength2Validator = input => validateForMinLength(2, input)
const noDefaultNastyWords = input => validateForNastyWords(['crap'], input)

const defaultTextValidators = [minLength2Validator, noDefaultNastyWords]

const validationConfigList2 = [
    "name", 
    "surname", 
    "city", 
    "zip", 
    "phone", 
    "street"
].map(field=>({field, validators: defaultTextValidators}))
//console.log(validationConfigList2)